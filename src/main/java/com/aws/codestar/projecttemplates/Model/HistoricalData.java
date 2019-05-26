package com.aws.codestar.projecttemplates.Model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.Objects;

public class HistoricalData {

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

  public HistoricalData() {
  }

//  @JsonCreator
  public HistoricalData(/*@JsonProperty("dateTime")*/ LocalDateTime dateTime,
      /*@JsonProperty("open")*/ double open, /*@JsonProperty("high") */double high,
      /*@JsonProperty("low") */double low,/* @JsonProperty("close")*/ double close) {
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
    HistoricalData historicalData = (HistoricalData) o;
    return Double.compare(historicalData.open, open) == 0 &&
        Double.compare(historicalData.high, high) == 0 &&
        Double.compare(historicalData.low, low) == 0 &&
        Double.compare(historicalData.close, close) == 0 &&
        Objects.equals(dateTime, historicalData.dateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dateTime, open, high, low, close);
  }

  @Override
  public String toString() {
    return "{" +
        "dateTime=" + dateTime +
        ", open=" + open +
        ", high=" + high +
        ", low=" + low +
        ", close=" + close +
        '}';
  }
}
