package com.aws.codestar.projecttemplates.Service;

import com.aws.codestar.projecttemplates.Model.ExchangeData;
import com.aws.codestar.projecttemplates.Model.ExchangeDataMapper;
import com.aws.codestar.projecttemplates.Model.HistoricalData;
import com.aws.codestar.projecttemplates.Model.HistoricalDataMapper;
import com.aws.codestar.projecttemplates.Model.SupportedCurrencySymbols;
import com.aws.codestar.projecttemplates.Validator.ArgumentValidator;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
  private final ExchangeDataMapper exchangeDataMapper;
  private final HistoricalDataMapper historicalDataMapper;
  private SupportedCurrencySymbols supportedCurrencySymbols;
  private ArgumentValidator argumentValidator;

  @Autowired
  public CurrencyService(ForeignExchange foreignExchange,
      ExchangeDataMapper exchangeDataMapper,
      HistoricalDataMapper historicalDataMapper,
      SupportedCurrencySymbols supportedCurrencySymbols,
      ArgumentValidator argumentValidator) {
    if (foreignExchange == null) {
      throw new IllegalArgumentException("foreignExchange cannot be null");
    }
    if (exchangeDataMapper == null) {
      throw new IllegalArgumentException("exchangeDataMapper cannot be null");
    }
    if (historicalDataMapper == null) {
      throw new IllegalArgumentException("historicalDataMapper cannot be null");
    }
    if (supportedCurrencySymbols == null) {
      throw new IllegalArgumentException("supportedCurrencySymbols cannot be null");
    }
    if (argumentValidator == null) {
      throw new IllegalArgumentException("argumentValidator cannot be null");
    }
    this.foreignExchange = foreignExchange;
    this.exchangeDataMapper = exchangeDataMapper;
    this.historicalDataMapper = historicalDataMapper;
    this.supportedCurrencySymbols = supportedCurrencySymbols;
    this.argumentValidator = argumentValidator;
  }

  public Set<String> getSupportedCurrencies() throws ServiceOperationException {
    try {
      log.debug("Getting supported currencies");
      return supportedCurrencySymbols.getSupportedSymbols();
    } catch (UnirestException e) {
      String message = "An error while getting supported currencies";
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  public ExchangeData getRateFromGivenCurrencies(String from, String to) throws ServiceOperationException {
    try {
      argumentValidator.ensureNotNull(from, "from");
      argumentValidator.ensureNotNull(to, "to");
      log.debug("Getting rate for symbols: from: {} - to: {}", from, to);
      CurrencyExchange currencyExchange = foreignExchange.currencyExchangeRate(from, to);
      CurrencyExchangeData currencyExchangeData = currencyExchange.getData();
      return exchangeDataMapper.mapExchange(currencyExchangeData);
    } catch (AlphaVantageException e) {
      String message = String.format("An error while getting rate for symbols: from %s - to %s", from, to);
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  public List<HistoricalData> getHistoricalDataForGivenCurrenciesAndRange(
      String from, String to, LocalDate fromDate, LocalDate toDate) throws ServiceOperationException {
    try {
      argumentValidator.ensureNotNull(from, "from");
      argumentValidator.ensureNotNull(to, "to");
      argumentValidator.ensureNotNull(fromDate, "fromDate");
      argumentValidator.ensureNotNull(toDate, "toDate");
      if (fromDate.isAfter(toDate)) {
        throw new IllegalArgumentException("toDate cannot be after fromDate");
      }
      log.debug("Getting forex data for symbols: from: {} - to: {}, and dates: fromDate: {} - toDate: {} ",
          from, to, fromDate, toDate);
      Daily response = chooseOutputSize(from, to, fromDate, toDate);
      List<ForexData> forexDataFromApi = response.getForexData();
      List<HistoricalData> historicalData = new ArrayList<>();
      for (ForexData fx : forexDataFromApi) {
        if (isDateInGivenRange(fromDate, toDate, fx)) {
          historicalData.add(historicalDataMapper.mapForexData(fx));
        }
      }
      return historicalData;
    } catch (AlphaVantageException e) {
      String message = String.format(
          "An error while getting forex data for symbols: from: %s - to: %s, and dates: fromDate: %s - toDate: %s",
          from, to, fromDate, toDate);
      log.error(message, e);
      throw new ServiceOperationException(message, e);
    }
  }

  private Daily chooseOutputSize(String from, String to, LocalDate fromDate, LocalDate toDate) {
    Daily response;
    if (toDate.minusDays(101).isAfter(fromDate)) {
      response = foreignExchange.daily(from, to, OutputSize.FULL);
    } else {
      response = foreignExchange.daily(from, to, OutputSize.COMPACT);
    }
    return response;
  }

  private boolean isDateInGivenRange(LocalDate fromDate, LocalDate toDate, ForexData fx) {
    return (fx.getDateTime().isEqual(convertToLocalDateTime(fromDate.minusDays(1)))
        || fx.getDateTime().isAfter(convertToLocalDateTime(fromDate.minusDays(1))))
        && (fx.getDateTime().isEqual(convertToLocalDateTime(toDate))
        || fx.getDateTime().isBefore(convertToLocalDateTime(toDate)));
  }

  private LocalDateTime convertToLocalDateTime(LocalDate date) {
    return date.atTime(LocalTime.now());
  }
}
