package com.aws.codestar.projecttemplates.Model;

public enum Ranges {

  Week(7),
  Month(30),
  Year(365),
  Full(2000);

  private int range;

  Ranges(int range) {
    this.range = range;
  }

  public int getRange() {
    return range;
  }

  public void setRange(int range) {
    this.range = range;
  }
}
