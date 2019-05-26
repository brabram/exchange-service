package com.aws.codestar.projecttemplates.Validator;

import com.aws.codestar.projecttemplates.Model.SupportedCurrencySymbols;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ArgumentValidator {

  private SupportedCurrencySymbols supportedCurrencySymbols;

  @Autowired
  public ArgumentValidator(SupportedCurrencySymbols supportedCurrencySymbols) {
    this.supportedCurrencySymbols = supportedCurrencySymbols;
  }

  public <T> void ensureNotNull(T argument, String paramName) {
    if (argument == null) {
      throw new IllegalArgumentException(String.format("%s cannot be null", paramName));
    }
  }

  public Boolean validateSymbol(String symbol) throws UnirestException {
    if (symbol == null) {
      return false;
    }
    List<String> supportedSymbols = new ArrayList<>(supportedCurrencySymbols.getSupportedSymbols());
    for (String currency : supportedSymbols) {
      if (currency.equals(symbol)) {
        return true;
      }
    }
    return false;
  }
  public Boolean validateDate(LocalDate fromDate, LocalDate toDate) {
    return fromDate != null && toDate != null && !fromDate.isAfter(toDate);
  }
}
