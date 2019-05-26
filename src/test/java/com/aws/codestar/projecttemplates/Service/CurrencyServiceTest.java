
package com.aws.codestar.projecttemplates.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patriques.ForeignExchange;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.exchange.CurrencyExchange;
import org.patriques.output.exchange.Daily;
import org.patriques.output.exchange.data.CurrencyExchangeData;
import org.patriques.output.exchange.data.ForexData;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock
  private ForeignExchange foreignExchange;

  @Mock
  private CurrencyExchange currencyExchange;

  @Mock
  private CurrencyExchangeData currencyExchangeData;

  @Mock
  private Daily response;

  @Mock
  private ExchangeDataMapper exchangeDataMapper;

  @Mock
  private HistoricalDataMapper historicalDataMapper;

  @Mock
  private ArgumentValidator argumentValidator;

  @Mock
  private SupportedCurrencySymbols supportedCurrencySymbols;

  @InjectMocks
  private CurrencyService currencyService;

  @Test
  void shouldGetRate() throws ServiceOperationException {
    //Given
    String from = "USD";
    String to = "PLN";
    float rate = 12;
    ExchangeData exchange = new ExchangeData(from, to, rate);
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doNothing().when(argumentValidator).ensureNotNull(to, "to");
    when(foreignExchange.currencyExchangeRate(from, to)).thenReturn(currencyExchange);
    when(currencyExchange.getData()).thenReturn(currencyExchangeData);
    when(exchangeDataMapper.mapExchange(currencyExchangeData)).thenReturn(exchange);

    //When
    ExchangeData actualExchange = currencyService.getRateFromGivenCurrencies(from, to);

    //Then
    assertEquals(exchange, actualExchange);
    verify(foreignExchange).currencyExchangeRate(from, to);
    verify(currencyExchange).getData();
    verify(exchangeDataMapper).mapExchange(currencyExchangeData);
  }

  @Test
  void shouldThrownIllegalArgumenExceptionWhenFromSymbolIsNull() {
    doThrow(IllegalArgumentException.class).when(argumentValidator).ensureNotNull(null, "from");
    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies(null, "USD"));
  }

  @Test
  void shouldThrownIllegalArgumenExceptionWhenToSymbolIsNull() {
    String from = "USD";
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doThrow(IllegalArgumentException.class).when(argumentValidator).ensureNotNull(null, "to");
    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies(from, null));
  }

  @Test
  void shouldThrownAlphaVantageExceptionWhenSomethingGoWrongOnServer() {
    //Given
    String from = "USD";
    String to = "PLN";
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doNothing().when(argumentValidator).ensureNotNull(to, "to");
    doThrow(AlphaVantageException.class).when(foreignExchange).currencyExchangeRate(from, to);

    //Then
    assertThrows(ServiceOperationException.class, () -> currencyService.getRateFromGivenCurrencies(from, to));
  }

  @Test
  void shouldGetHistoricalData() throws ServiceOperationException {
    //Given
    String from = "USD";
    String to = "PLN";
    LocalDate fromDate = LocalDate.of(2019, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 20);
    LocalDateTime dateTime = LocalDate.of(2019, 5, 20).atTime(LocalTime.of(0, 0, 0));
    double open = 1;
    double high = 2;
    double low = 3;
    double close = 4;
    HistoricalData mappedHistoricalData = new HistoricalData(dateTime, open, high, low, close);
    List<HistoricalData> mappedHistoricalDataList = Collections.singletonList(mappedHistoricalData);
    ForexData forexData = new ForexData(dateTime, open, high, low, close);
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doNothing().when(argumentValidator).ensureNotNull(to, "to");
    doNothing().when(argumentValidator).ensureNotNull(fromDate, "fromDate");
    doNothing().when(argumentValidator).ensureNotNull(toDate, "toDate");
    when(foreignExchange.daily(from, to, OutputSize.COMPACT)).thenReturn(response);
    when(response.getForexData()).thenReturn(Collections.singletonList(forexData));
    when(historicalDataMapper.mapForexData(forexData)).thenReturn(mappedHistoricalData);

    //When
    List<HistoricalData> actualHistoricalDataList = currencyService.getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate);

    //Then
    assertEquals(mappedHistoricalDataList, actualHistoricalDataList);
  }

  @Test
  void shouldThrownIllegalArgumenExceptionWhenFromDateIsAfterToDate() {
    String from = "USD";
    String to = "PLN";
    LocalDate fromDate = LocalDate.of(2020, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 20);
    LocalDateTime dateTime = LocalDate.of(2019, 5, 20).atTime(LocalTime.of(0, 0, 0));
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doNothing().when(argumentValidator).ensureNotNull(to, "to");
    doNothing().when(argumentValidator).ensureNotNull(fromDate, "fromDate");
    doNothing().when(argumentValidator).ensureNotNull(toDate, "toDate");
    assertThrows(IllegalArgumentException.class,
        () -> currencyService.getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate));
  }

  @Test
  void shouldThrownAlpgaVantageExceptionWhenSomethingIsWrong() {
    String from = "USD";
    String to = "PLN";
    LocalDate fromDate = LocalDate.of(2019, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 20);
    LocalDateTime dateTime = LocalDate.of(2019, 5, 20).atTime(LocalTime.of(0, 0, 0));
    double open = 1;
    double high = 2;
    double low = 3;
    double close = 4;
    ForexData forexData = new ForexData(dateTime, open, high, low, close);
    doNothing().when(argumentValidator).ensureNotNull(from, "from");
    doNothing().when(argumentValidator).ensureNotNull(to, "to");
    doNothing().when(argumentValidator).ensureNotNull(fromDate, "fromDate");
    doNothing().when(argumentValidator).ensureNotNull(toDate, "toDate");
    when(foreignExchange.daily(from, to, OutputSize.COMPACT)).thenReturn(response);
    when(response.getForexData()).thenReturn(Collections.singletonList(forexData));
    doThrow(AlphaVantageException.class).when(historicalDataMapper).mapForexData(forexData);

    //Then
    assertThrows(ServiceOperationException.class, ()
        -> currencyService.getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate));
  }

  @Test
  void shouldGetSupportedCurrencies() throws UnirestException, ServiceOperationException {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);

    //When
    Set<String> actualCurrencies = currencyService.getSupportedCurrencies();

    //Then
    assertEquals(currencies, actualCurrencies);
  }

  @Test
  void shouldThrowUnirestExceptionWhenSomethingIsWrong() throws UnirestException, ServiceOperationException {
    //Given
    doThrow(UnirestException.class).when(supportedCurrencySymbols).getSupportedSymbols();

    //Then
    assertThrows(ServiceOperationException.class, () -> currencyService.getSupportedCurrencies());
  }
}

