package com.aws.codestar.projecttemplates.Service;

import com.aws.codestar.projecttemplates.Model.CurrencyExchangeData;
import com.aws.codestar.projecttemplates.Model.CurrencyExchangeDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexData;
import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.patriques.ForeignExchange;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.exchange.CurrencyExchange;
import org.patriques.output.exchange.Daily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

  private static Logger log = LoggerFactory.getLogger(CurrencyService.class);
  private ForeignExchange foreignExchange;
  private final CurrencyExchangeDataMapper currencyExchangeDataMapper;
  private final ForexDataMapper forexDataMapper;

  @Autowired
  public CurrencyService(ForeignExchange foreignExchange,
      CurrencyExchangeDataMapper currencyExchangeDataMapper,
      ForexDataMapper forexDataMapper) {
    if (foreignExchange == null) {
      throw new IllegalArgumentException("foreignExchange cannot be null");
    }
    if (currencyExchangeDataMapper == null) {
      throw new IllegalArgumentException("currencyExchangeDataMapper cannot be null");
    }
    if (forexDataMapper == null) {
      throw new IllegalArgumentException("forexDataMapper cannot be null");
    }
    this.foreignExchange = foreignExchange;
    this.currencyExchangeDataMapper = currencyExchangeDataMapper;
    this.forexDataMapper = forexDataMapper;
  }

  public CurrencyExchangeData getRateFromGivenCurrencies(String from, String to) throws ServiceOperationException {
    if (from.equals("")) {
      throw new IllegalArgumentException("'from' symbol cannot be null");
    }
    if (to.equals("")) {
      throw new IllegalArgumentException("'to' symbol cannot be null");
    }
    log.debug("Getting rate for symbols: from: {} - to: {}", from, to);
    if (!isSymbolPresentOnTheList(from)) {
      throw new IllegalArgumentException("unsupported 'from' symbol");
    }
    if (!isSymbolPresentOnTheList(to)) {
      throw new IllegalArgumentException("unsupported 'to' symbol");
    }
    CurrencyExchange currencyExchange = foreignExchange.currencyExchangeRate(from, to);
    org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData = currencyExchange.getData();
    return currencyExchangeDataMapper.mapExchange(currencyExchangeData);
  }

  private Boolean isSymbolPresentOnTheList(String symbol) throws ServiceOperationException {
    for (String currency : getSupportedCurrencies()) {
      if (currency.equals(symbol)) {
        return true;
      }
    }
    return false;
  }

  public List<ForexData> getHistoricalDataForGivenCurrenciesAndRange(
      String from, String to, LocalDateTime fromDate, LocalDateTime toDate) throws ServiceOperationException {
    if (from.equals("")) {
      throw new IllegalArgumentException("'from' symbol cannot be null");
    }
    if (to.equals("")) {
      throw new IllegalArgumentException("'to' symbol cannot be null");
    }
    if (fromDate == null) {
      throw new IllegalArgumentException("'fromDate' cannot be null");
    }
    if (toDate == null) {
      throw new IllegalArgumentException("'toDate' cannot be null");
    }
    if (toDate.isBefore(fromDate)) {
      throw new IllegalArgumentException("'toDate' cannot be before 'fromDate'.");
    }
    try {
      Daily response = foreignExchange.daily(from, to, OutputSize.FULL);
      List<org.patriques.output.exchange.data.ForexData> forexDataFromApi = response.getForexData();
      List<ForexData> forexData = new ArrayList<>();
      for (org.patriques.output.exchange.data.ForexData fx : forexDataFromApi) {
        forexData.add(forexDataMapper.mapForexData(fx));
      }
      return forexData
          .stream()
          .filter(data -> data.getDateTime()
              .compareTo(toDate) <= 0 && data.getDateTime().compareTo(fromDate.minusDays(1L)) >= 0)
          .collect(Collectors.toList());
    } catch (AlphaVantageException e) {
      String message = "An error while getting forex data";
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  public Set<String> getSupportedCurrencies() throws ServiceOperationException {
    try {
      HttpResponse<JsonNode> jsonResponse = Unirest.get("https://openexchangerates.org/api/currencies.json")
          .asJson();
      return jsonResponse.getBody().getObject().keySet();
    } catch (UnirestException e) {
      String message = "An error while getting supported currencies";
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }
}
