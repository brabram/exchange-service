//
//package com.aws.codestar.projecttemplates.Service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.aws.codestar.projecttemplates.Model.ExchangeDataMapper;
//import com.aws.codestar.projecttemplates.Model.ExchangeData;
//import com.aws.codestar.projecttemplates.Model.HistoricalDataMapper;
//import com.aws.codestar.projecttemplates.Model.HistoricalData;
//import com.aws.codestar.projecttemplates.Validator.ArgumentValidator;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Stream;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.patriques.ForeignExchange;
//import org.patriques.input.timeseries.OutputSize;
//import org.patriques.output.AlphaVantageException;
//import org.patriques.output.exchange.CurrencyExchange;
//import org.patriques.output.exchange.Daily;
//import org.patriques.output.exchange.data.ForexData;
//
//@ExtendWith(MockitoExtension.class)
//class CurrencyServiceTest {
//
//  @Mock
//  private ForeignExchange foreignExchange;
//
//  @Mock
//  private CurrencyExchange currencyExchange;
//
//  @Mock
//  private org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData;
//
//  @Mock
//  private Daily response;
//
//  @Mock
//  private ExchangeDataMapper exchangeDataMapper;
//
//  @Mock
//  private HistoricalDataMapper historicalDataMapper;
//
//  @Mock
//  private ArgumentValidator argumentValidator;
//
//  @InjectMocks
//  private CurrencyService currencyService;
//
//  @Test
//  void shouldGetRate() throws ServiceOperationException {
//    //Given
//    String from = "USD";
//    String to = "PLN";
//    float rate = 12;
//    argumentValidator.ensureNotNull(from, "from");
//    argumentValidator.ensureNotNull(to, "to");
//    ExchangeData exchange = new ExchangeData(from, to, rate);
//    when(foreignExchange.currencyExchangeRate(from, to)).thenReturn(currencyExchange);
//    when(currencyExchange.getData()).thenReturn(currencyExchangeData);
//    when(exchangeDataMapper.mapExchange(currencyExchangeData)).thenReturn(exchange);
//
//    //When
//    ExchangeData actualExchange = currencyService.getRateFromGivenCurrencies(from, to);
//
//    //Then
//    assertEquals(exchange, actualExchange);
//    verify(foreignExchange).currencyExchangeRate(from, to);
//    verify(currencyExchange).getData();
//    verify(exchangeDataMapper).mapExchange(currencyExchangeData);
//  }
//
//  @Test
//  void shouldThrownIllegalArgumenExceptionWhenFromSymbolIsNull() {
//    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies("", "PLN"));
//  }
//
//  @Test
//  void shouldThrownIllegalArgumenExceptionWhenToSymbolIsNull() {
//    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies("PLN", ""));
//  }
//
//  @Test
//  void shouldThrownAlphaVantageExceptionWhenSomethingGoWrongOnServer() {
//    //Given
//    String from = "USD";
//    String to = "PLN";
//    doThrow(AlphaVantageException.class).when(foreignExchange).currencyExchangeRate(from, to);
//
//    //Then
//    assertThrows(ServiceOperationException.class, () -> currencyService.getRateFromGivenCurrencies(from, to));
//  }
//
//  @Test
//  void shouldGetHistoricalData() throws ServiceOperationException {
//    //Given
//    String from = "USD";
//    String to = "PLN";
//    LocalDate fromDate = LocalDate.of(2019, 5, 20);
//    LocalDate toDate = LocalDate.of(2019, 5, 20);
//    LocalDateTime dateTime = LocalDate.of(2019,5,20).atTime(LocalTime.of(0,0,0));
//    double open = 1;
//    double high = 2;
//    double low = 3;
//    double close = 4;
//    HistoricalData mappedHistoricalData = new HistoricalData(dateTime, open, high, low, close);
//    List<HistoricalData> mappedHistoricalDataList = Collections.singletonList(mappedHistoricalData);
//    ForexData forexData = new ForexData(dateTime, open, high, low, close);
//    when(foreignExchange.daily(from, to, OutputSize.COMPACT)).thenReturn(response);
//    when(response.getForexData()).thenReturn(Collections.singletonList(forexData));
//    when(historicalDataMapper.mapForexData(forexData)).thenReturn(mappedHistoricalData);
//
//    //When
//    List<HistoricalData> actualHistoricalDataList = currencyService.getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate);
//
//    //Then
//    assertEquals(mappedHistoricalDataList, actualHistoricalDataList);
//  }
//
//  /*
//  @ParameterizedTest
//  @MethodSource("symbolArguments")
//  void shouldValidateSymbol(String symbol, String expectedResult) throws ServiceOperationException {
//    //When
//    String result = currencyService.validateSymbol(symbol);
//
//    //Then
//    assertEquals(expectedResult, result);
//  }
//
//  private static Stream<Arguments> symbolArguments() {
//    return Stream.of(
//        Arguments.of("", "currency symbol cannot be null"),
//        Arguments.of("USD", null),
//        Arguments.of("PLN", null),
//        Arguments.of("xxx", "Passed symbol is incorrect: xxx")
//    );
//  }
//  */
//
//  @ParameterizedTest
//  @MethodSource("dateArguments")
//  void shouldValidateDate(LocalDate fromDate, LocalDate toDate, String expectedResult) throws ServiceOperationException {
//    //When
//    String result = currencyService.validateDate(fromDate, toDate);
//
//    //Then
//    assertEquals(expectedResult, result);
//  }
//
//  private static Stream<Arguments> dateArguments() {
//    return Stream.of(
//        Arguments.of(null, LocalDate.now(), "from date cannot be null"),
//        Arguments.of(LocalDate.now(), null, "to date cannot be null"),
//        Arguments.of(LocalDate.of(2019, 5, 20), LocalDate.of(2019, 5, 21), null),
//        Arguments.of(LocalDate.of(2019, 5, 20), LocalDate.of(2019, 5, 10)
//            , "to date cannot be after from date")
//    );
//  }
//}
//
