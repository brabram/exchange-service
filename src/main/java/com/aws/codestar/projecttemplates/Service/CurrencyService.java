package com.aws.codestar.projecttemplates.Service;

import com.aws.codestar.projecttemplates.Model.CurrencyExchangeData;
import com.aws.codestar.projecttemplates.Model.CurrencyExchangeDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexData;
import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

  public Set<String> getSupportedCurrencies() throws ServiceOperationException {
    try {
      log.debug("Getting supported currencies");
      HttpResponse<JsonNode> jsonResponse = Unirest.get("https://openexchangerates.org/api/currencies.json")
          .asJson();
      return jsonResponse.getBody().getObject().keySet();
    } catch (UnirestException e) {
      String message = "An error while getting supported currencies";
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  public CurrencyExchangeData getRateFromGivenCurrencies(String from, String to) throws ServiceOperationException {
    log.debug("Getting rate for symbols: from: {} - to: {}", from, to);
    CurrencyExchange currencyExchange = foreignExchange.currencyExchangeRate(from, to);
    org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData = currencyExchange.getData();
    return currencyExchangeDataMapper.mapExchange(currencyExchangeData);
  }

  public List<ForexData> getHistoricalDataForGivenCurrenciesAndRange(
      String from, String to, LocalDate fromDate, LocalDate toDate) throws ServiceOperationException {
    try {
      log.debug("Getting forex data for symbols: from: {} - to: {}, and dates: fromDate: {} - toDate: {} ",
          from, to, fromDate, toDate);
      Daily response = foreignExchange.daily(from, to, OutputSize.FULL);
      List<org.patriques.output.exchange.data.ForexData> forexDataFromApi = response.getForexData();
      List<ForexData> forexData = new ArrayList<>();
      for (org.patriques.output.exchange.data.ForexData fx : forexDataFromApi) {
        forexData.add(forexDataMapper.mapForexData(fx));
      }
      return forexData
          .stream()
          .filter(data -> data.getDateTime()
              .compareTo(convertToLocalDateTime(toDate)) <= 0 && data.getDateTime().compareTo(convertToLocalDateTime(fromDate.minusDays(1L))) >= 0)
          .collect(Collectors.toList());
    } catch (AlphaVantageException e) {
      String message = "An error while getting forex data";
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  private LocalDateTime convertToLocalDateTime(LocalDate date) {
    return date.atTime(LocalTime.now());
  }

  public String validateSymbol(String symbol) throws ServiceOperationException {
    if (symbol.equals("")) {
      return "currency symbol cannot be null";
    }
    for (String currency : getSupportedCurrencies()) {
      if (currency.equals(symbol)) {
        return null;
      }
    }
    return String.format("Passed symbol is incorrect: %s", symbol);
  }

  public String validateDate(LocalDate fromDate, LocalDate toDate) throws ServiceOperationException {
    if (fromDate == null) {
      return "from date cannot be null";
    }
    if (toDate == null) {
      return "to date cannot be null";
    }
    if (fromDate.isAfter(toDate)) {
      return "to date cannot be after from date";
    }
    return null;
  }
}
