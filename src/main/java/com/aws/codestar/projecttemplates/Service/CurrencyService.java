package com.aws.codestar.projecttemplates.Service;

import com.aws.codestar.projecttemplates.Configuration.ApplicationConfiguration;
import com.aws.codestar.projecttemplates.Model.Exchange;
import com.aws.codestar.projecttemplates.Model.Ranges;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.patriques.ForeignExchange;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.exchange.CurrencyExchange;
import org.patriques.output.exchange.Daily;
import org.patriques.output.exchange.data.CurrencyExchangeData;
import org.patriques.output.exchange.data.ForexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  private static Logger log = LoggerFactory.getLogger(CurrencyService.class);
  private ForeignExchange foreignExchange;

  @Autowired
  public CurrencyService() {
    this.foreignExchange = new ApplicationConfiguration().getForeignExchange();
  }

  public Exchange getRateFromGivenCurrencies(String from, String to) {
    log.debug("Getting rate for symbols: from: {} - to: {}", from, to);
//    if (isSymbolPresentOnTheList(from) && isSymbolPresentOnTheList(to)) {
      CurrencyExchange currencyExchange = foreignExchange.currencyExchangeRate(from, to);
      CurrencyExchangeData currencyExchangeData = currencyExchange.getData();
      float rate = currencyExchangeData.getExchangeRate();
      return new Exchange(from, to, rate);
//    }
//    return new Exchange(from, to, -1);
  }

//  private Boolean isSymbolPresentOnTheList(String symbol) {
//    for (Currencies currency : Currencies.values()) {
//      if (currency.getSymbol().equals(symbol)) {
//        return true;
//      }
//    }
//    return false;
//  }

  public List<ForexData> getHistoricalDataForGivenCurrenciesAndRange(String from, String to, Ranges ranges) {
    try {
      Daily response = foreignExchange.daily(from, to, OutputSize.FULL);
      List<ForexData> forexData = response.getForexData();
      LocalDateTime now = LocalDateTime.now();
      if (ranges == Ranges.Full) {
        return forexData;
      }
      if (ranges == Ranges.Week) {
        LocalDateTime week = LocalDateTime.now().minusDays(7);
        return forexData.stream()
            .filter(data -> data.getDateTime().compareTo(week) >= 0 && data.getDateTime().compareTo(now) <= 0)
            .collect(Collectors.toList());
      }
      if (ranges == Ranges.Month) {
        LocalDateTime month = LocalDateTime.now().minusDays(30);
        return forexData.stream()
            .filter(data -> data.getDateTime().compareTo(month) >= 0 && data.getDateTime().compareTo(now) <= 0)
            .collect(Collectors.toList());
      }
      if (ranges == Ranges.Year) {
        LocalDateTime year = LocalDateTime.now().minusDays(365);
        return forexData.stream()
            .filter(data -> data.getDateTime().compareTo(year) >= 0 && data.getDateTime().compareTo(now) <= 0)
            .collect(Collectors.toList());
      }
    } catch (AlphaVantageException e) {
      System.out.println("something went wrong");
    }
    return new ArrayList<>();
  }
}

