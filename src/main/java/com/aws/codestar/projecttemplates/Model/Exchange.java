package com.aws.codestar.projecttemplates.Model;

import java.util.Objects;

public class Exchange {

  private String from;
  private String to;
  private float rate;

  public Exchange() {
  }

  public Exchange(String from, String to, float rate) {
    this.from = from;
    this.to = to;
    this.rate = rate;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public float getRate() {
    return rate;
  }

  public void setRate(float rate) {
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Exchange exchange = (Exchange) o;
    return Float.compare(exchange.rate, rate) == 0 &&
        Objects.equals(from, exchange.from) &&
        Objects.equals(to, exchange.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, rate);
  }
}
