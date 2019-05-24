package com.aws.codestar.projecttemplates.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

public class CurrencyExchangeData {

  @ApiModelProperty(value = "currency symbol", example = "EUR")
  private String from;

  @ApiModelProperty(value = "currency symbol", example = "PLN")
  private String to;

  @ApiModelProperty(value = "exchange rate", example = "4.2923")
  private float rate;

  public CurrencyExchangeData() {
  }

  @JsonCreator
  public CurrencyExchangeData(@JsonProperty("from") String from, @JsonProperty("to") String to, @JsonProperty("rate") float rate) {
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
    CurrencyExchangeData currencyExchangeData = (CurrencyExchangeData) o;
    return Float.compare(currencyExchangeData.rate, rate) == 0 &&
        Objects.equals(from, currencyExchangeData.from) &&
        Objects.equals(to, currencyExchangeData.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, rate);
  }

  @Override
  public String toString() {
    return "CurrencyExchangeData{" +
        "from='" + from + '\'' +
        ", to='" + to + '\'' +
        ", rate=" + rate +
        '}';
  }
}
