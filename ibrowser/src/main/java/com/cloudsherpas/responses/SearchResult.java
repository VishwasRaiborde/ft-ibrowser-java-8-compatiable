package com.cloudsherpas.responses;

import java.util.List;

import com.cloudsherpas.domain.Report;

public class SearchResult {
  public List<Report> divisional;
  public List<Report> branch;
  public List<Report> buying;

  public List<Report> getDivisional() {
    return divisional;
  }

  public void setDivisional(List<Report> divisional) {
    this.divisional = divisional;
  }

  public List<Report> getBranch() {
    return branch;
  }

  public void setBranch(List<Report> branch) {
    this.branch = branch;
  }

  public List<Report> getBuying() {
    return buying;
  }

  public void setBuying(List<Report> buying) {
    this.buying = buying;
  }

}
