package com.aws.codestar.projecttemplates.Service;

import com.aws.codestar.projecttemplates.Model.CurrencyExchangeData;
import com.aws.codestar.projecttemplates.Model.CurrencyExchangeDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexData;
import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    this.foreignExchange = foreignExchange;
    this.currencyExchangeDataMapper = currencyExchangeDataMapper;
    this.forexDataMapper = forexDataMapper;
  }

  public CurrencyExchangeData getRateFromGivenCurrencies(String from, String to) {
    log.debug("Getting rate for symbols: from: {} - to: {}", from, to);
//    if (isSymbolPresentOnTheList(from) && isSymbolPresentOnTheList(to)) {
    CurrencyExchange currencyExchange = foreignExchange.currencyExchangeRate(from, to);
    org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData = currencyExchange.getData();
    return currencyExchangeDataMapper.mapExchange(currencyExchangeData);
//    }
//    return new CurrencyExchangeData(from, to, -1);
  }

//  private Boolean isSymbolPresentOnTheList(String symbol) {
//    for (Currencies currency : Currencies.values()) {
//      if (currency.getSymbol().equals(symbol)) {
//        return true;
//      }
//    }
//    return false;
//  }

  public List<ForexData> getHistoricalDataForGivenCurrenciesAndRange(
      String from, String to, LocalDateTime fromDate, LocalDateTime toDate) {
    try {
      Daily response = foreignExchange.daily(from, to, OutputSize.FULL);
      List<org.patriques.output.exchange.data.ForexData> forexDataFromApi = response.getForexData();
      List<ForexData> forexData = new ArrayList<>();
      for (org.patriques.output.exchange.data.ForexData fx : forexDataFromApi) {
        forexData.add(forexDataMapper.mapForexData(fx));
      }
      return forexData;
    } catch (AlphaVantageException e) {
      System.out.println("something went wrong");
    }
    return new ArrayList<>();
  }

  public Set<String> getSupportedCurrencies() throws IOException, UnirestException {
    HttpResponse<JsonNode> jsonResponse = Unirest.get("https://openexchangerates.org/api/currencies.json")
        .asJson();
    return jsonResponse.getBody().getObject().keySet();
  }
}
