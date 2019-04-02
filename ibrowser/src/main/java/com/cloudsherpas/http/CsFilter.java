package com.cloudsherpas.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.cloudsherpas.GlobalConstants;
import com.cloudsherpas.dao.GoogleGroupMemberDao;
import com.cloudsherpas.dao.GroupDao;
import com.cloudsherpas.dao.UserDao;
import com.cloudsherpas.domain.AppUser;
import com.cloudsherpas.domain.GoogleGroup;
import com.cloudsherpas.domain.GoogleGroupMember;
import com.cloudsherpas.google.api.DirectoryApi;
import com.cloudsherpas.model.IPRangeItem;
import com.google.inject.Inject;

public class CsFilter implements Filter, GlobalConstants {

  protected boolean validIP = false;
  private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  public final UserDao userDao;
  public final GroupDao groupDao;
  public final GoogleGroupMemberDao memberDao;

  @Inject
  public CsFilter(UserDao userDao, GroupDao groupDao, GoogleGroupMemberDao memberDao) {
    this.userDao = userDao;
    this.groupDao = groupDao;
    this.memberDao = memberDao;
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    // HttpServletRequest request = (HttpServletRequest)req;
//		validIPRange(request);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // TODO Auto-generated method stub

  }

  /**
   * Validate requested client IP to the application for allow/deny in the certain IP ranges
   * 
   * @param request
   * @throws UnknownHostException
   */
  protected boolean isValidIPRange(HttpServletRequest request) throws UnknownHostException {
    String clientIPAdderss = request.getHeader("X-FORWARDED-FOR"); // Load balancer sets this header
                                                                   // client's IP
    logger.log(Level.INFO, "IP from header :" + clientIPAdderss);

    if (clientIPAdderss == null) {
      clientIPAdderss = request.getRemoteAddr();
      logger.log(Level.INFO, "IP from remote addr :" + clientIPAdderss);
      if ((clientIPAdderss != null && clientIPAdderss.contains("."))) {

        // if (!IP_RANGES.isEmpty()) {
        for (IPRangeItem ipRange : IP_RANGES) {

          long ipLower = ipToLong(InetAddress.getByName(ipRange.getLowerRange()));
          long ipHigher = ipToLong(InetAddress.getByName(ipRange.getHigherRange()));
          long ipClient = ipToLong(InetAddress.getByName(clientIPAdderss));

          if ((ipLower <= ipClient && ipClient <= ipHigher)) {
//        		    		validIP = true;
            logger.log(Level.INFO, "TRUE");
            return true;
          }
        }
        // }

//                validIP = true;
//               return;
      }
    }

    logger.log(Level.INFO, "FALSE");
    // TODO hack to work with locak vishwas return false;
    return true;

  }

  protected boolean isValidIP() {
    return validIP;
  }

  public long ipToLong(InetAddress ip) {
    byte[] octets = ip.getAddress();
    long result = 0;
    for (byte octet : octets) {
      result <<= 8;
      result |= octet & 0xff;
    }
    return result;
  }

  public void syncUsersGroups(AppUser appUser) {

    DirectoryApi directoryApi;
    directoryApi = new DirectoryApi();
    Set<String> groups = new HashSet<String>();

    String email = appUser.getEmail();
    directoryApi.checkGroupMember(email);
    List<String> userGroups = directoryApi.getUserGroups(email);
    logger.log(Level.INFO, "List of groups found for " + email + ":" + userGroups.toString());
    for (String userGroup : userGroups) {
      logger.log(Level.INFO, "Checking user's memnbership to group: " + userGroup);
      if (userGroup == null || "".equals(userGroup.trim())
          || (!userGroup.toLowerCase().startsWith("_JL Branch:".toLowerCase())
              && !userGroup.toLowerCase().startsWith("Branch:".toLowerCase())
              && !userGroup.toLowerCase().startsWith("iBr".toLowerCase())
              && !userGroup.toLowerCase().startsWith("_JL iBr".toLowerCase())
              && !userGroup.toLowerCase().startsWith("iBrowser".toLowerCase()))) {
        continue;
      }

      GoogleGroupMember member = null;
      GoogleGroup gGroup = groupDao.getGroupByCode(userGroup.toUpperCase());
      if (gGroup != null) {
        member = memberDao.getGoogleGroupMemberByParentEmail(gGroup.getEmail());
      }
      if (member != null) {
        for (String pemail : member.getParentEmails()) {
          GoogleGroup pGroup = groupDao.getGroupByEmail(pemail);
          if (pGroup != null && pGroup.getName() != null && !"".equals(pGroup.getName().trim())) {
            groups.add(pGroup.getName());
          }
        }
      }
      groups.add(userGroup);
    }
    if (!groups.isEmpty()) {
      List<String> listGroups = new ArrayList<String>(groups);
      Collections.sort(listGroups);
      appUser.setGroups(listGroups);
      userDao.persist(appUser);
      for (String g : groups) {
        logger.log(Level.INFO, "Users new synced group: " + g);
      }
      logger.log(Level.INFO, "Groups synced up for the user: " + appUser.getEmail());
    } else {
      logger.log(Level.INFO, "No groups found for user " + email);
    }
  }

}
