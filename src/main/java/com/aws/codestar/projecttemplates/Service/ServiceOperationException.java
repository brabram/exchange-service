package com.aws.codestar.projecttemplates.Service;

public class ServiceOperationException extends Exception {

  public ServiceOperationException() {
  }

  public ServiceOperationException(String message) {
    super(message);
  }

  public ServiceOperationException(Throwable cause) {
    super(cause);
  }

  public ServiceOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
