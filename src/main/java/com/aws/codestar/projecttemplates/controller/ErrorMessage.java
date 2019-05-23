package com.aws.codestar.projecttemplates.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ErrorMessage {

  private final String message;
  private final List<String> details;

  public ErrorMessage(String message) {
    this.message = message;
    details = new ArrayList<>();
  }

  @JsonCreator
  public ErrorMessage(@JsonProperty("message") String message, @JsonProperty("details") List<String> details) {
    this.message = message;
    this.details = details;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getDetails() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorMessage that = (ErrorMessage) o;
    return Objects.equals(message, that.message)
        && Objects.equals(details, that.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, details);
  }

  @Override
  public String toString() {
    return String.format("message : %s, details : %s", message, details);
  }

}
