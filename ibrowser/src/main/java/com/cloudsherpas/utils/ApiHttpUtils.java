package com.cloudsherpas.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudsherpas.responses.ErrorResponse;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class ApiHttpUtils {

  private final Logger logger;
  private final Gson gson;

  @Inject
  public ApiHttpUtils(Logger logger, Gson gson) {
    super();
    this.logger = logger;
    this.gson = gson;
  }

  @SuppressWarnings("unchecked")
  public Object readPost(HttpServletRequest req, @SuppressWarnings("rawtypes") Class clazz)
      throws IOException {
    String data = new String(ByteStreams.toByteArray(req.getInputStream()));
    logger.fine("Post: \n" + data);
    return gson.fromJson(data, clazz);
  }

  public void sendResponse(HttpServletResponse resp, Object obj) throws IOException {
    PrintWriter out = resp.getWriter();
    String json = gson.toJson(obj);
    resp.setContentType("application/json");
    out.print(json);
    out.flush();
    out.close();
  }

  public void sendError(HttpServletResponse resp, int code, String message) throws IOException {
    resp.setStatus(code);
    sendResponse(resp, new ErrorResponse(code, message));
  }

  public void postBackMessage(HttpServletResponse resp, Object obj) throws IOException {
    PrintWriter out = resp.getWriter();
    String json = gson.toJson(obj);
    resp.setContentType("text/html");
    out.println("<html>");
    out.println("<head>");
    out.println("<script>");
    out.print("var response = ");
    out.print(json);
    out.println(";");
    out.println(
        "function postResponse() { window.parent.postMessage(JSON.stringify(response), '*'); }");
    out.println("</script>");
    out.println("</head>");
    out.println("<body onload=\"postResponse();\">");
    out.println("</body>");
    out.println("</html>");

    out.flush();
    out.close();
  }

  public void postBackError(HttpServletResponse resp, int code, String message) throws IOException {
    resp.setStatus(code);
    postBackMessage(resp, new ErrorResponse(code, message));
  }

}
