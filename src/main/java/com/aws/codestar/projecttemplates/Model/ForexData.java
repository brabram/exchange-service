package com.aws.codestar.projecttemplates.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.Objects;

public class ForexData {

  @ApiModelProperty(value = "date of given rates", example = "2019-05-24")
  private LocalDateTime dateTime;

  @ApiModelProperty(value = "open value of exchange rate", example = "4.3025")
  private double open;

  @ApiModelProperty(value = "high value of eschange rate", example = "4.306")
  private double high;

  @ApiModelProperty(value = "low value of exchange rate", example = "4.3011")
  private double low;

  @ApiModelProperty(value = "close alue of exchange rate", example = "4.304")
  private double close;

  public ForexData() {
  }

  @JsonCreator
  public ForexData(LocalDateTime dateTime, double open, double high, double low, double close) {
    this.dateTime = dateTime;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public double getOpen() {
    return open;
  }

  public void setOpen(double open) {
    this.open = open;
  }

  public double getHigh() {
    return high;
  }

  public void setHigh(double high) {
    this.high = high;
  }

  public double getLow() {
    return low;
  }

  public void setLow(double low) {
    this.low = low;
  }

  public double getClose() {
    return close;
  }

  public void setClose(double close) {
    this.close = close;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ForexData forexData = (ForexData) o;
    return Double.compare(forexData.open, open) == 0 &&
        Double.compare(forexData.high, high) == 0 &&
        Double.compare(forexData.low, low) == 0 &&
        Double.compare(forexData.close, close) == 0 &&
        Objects.equals(dateTime, forexData.dateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dateTime, open, high, low, close);
  }

  @Override
  public String toString() {
    return "ForexData{" +
        "dateTime=" + dateTime +
        ", open=" + open +
        ", high=" + high +
        ", low=" + low +
        ", close=" + close +
        '}';
  }
}
