
package com.aws.codestar.projecttemplates.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aws.codestar.projecttemplates.Model.CurrencyExchangeData;
import com.aws.codestar.projecttemplates.Model.CurrencyExchangeDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patriques.ForeignExchange;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.exchange.CurrencyExchange;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock
  private ForeignExchange foreignExchange;

  @Mock
  private CurrencyExchange currencyExchange;

  @Mock
  private org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData;

  @Mock
  private CurrencyExchangeDataMapper currencyExchangeDataMapper;

  @Mock
  private ForexDataMapper forexDataMapper;

  @InjectMocks
  private CurrencyService currencyService;

  @Test
  void shouldGetRate() throws ServiceOperationException {
    //Given
    String from = "USD";
    String to = "PLN";
    float rate = 12;
    CurrencyExchangeData exchange = new CurrencyExchangeData(from, to, rate);
    when(foreignExchange.currencyExchangeRate(from, to)).thenReturn(currencyExchange);
    when(currencyExchange.getData()).thenReturn(currencyExchangeData);
    when(currencyExchangeDataMapper.mapExchange(currencyExchangeData)).thenReturn(exchange);

    //When
    CurrencyExchangeData actualExchange = currencyService.getRateFromGivenCurrencies(from, to);

    //Then
    assertEquals(exchange, actualExchange);
    verify(foreignExchange).currencyExchangeRate(from, to);
    verify(currencyExchange).getData();
    verify(currencyExchangeDataMapper).mapExchange(currencyExchangeData);
  }

  @Test
  void shouldThrownIllegalArgumenExceptionWhenFromSymbolIsNull() {
    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies("", "PLN"));
  }

  @Test
  void shouldThrownIllegalArgumenExceptionWhenToSymbolIsNull() {
    assertThrows(IllegalArgumentException.class, () -> currencyService.getRateFromGivenCurrencies("PLN", ""));
  }

  @Test
  void shouldThrownAlphaVantageExceptionWhenSomethingGoWrongOnServer() {
    //Given
    String from = "USD";
    String to = "PLN";
    doThrow(AlphaVantageException.class).when(foreignExchange).currencyExchangeRate(from, to);

    //Then
    assertThrows(ServiceOperationException.class, () -> currencyService.getRateFromGivenCurrencies(from, to));
  }

  /*
  @ParameterizedTest
  @MethodSource("symbolArguments")
  void shouldValidateSymbol(String symbol, String expectedResult) throws ServiceOperationException {
    //When
    String result = currencyService.validateSymbol(symbol);

    //Then
    assertEquals(expectedResult, result);
  }

  private static Stream<Arguments> symbolArguments() {
    return Stream.of(
        Arguments.of("", "currency symbol cannot be null"),
        Arguments.of("USD", null),
        Arguments.of("PLN", null),
        Arguments.of("xxx", "Passed symbol is incorrect: xxx")
    );
  }
  */

  @ParameterizedTest
  @MethodSource("dateArguments")
  void shouldValidateDate(LocalDate fromDate, LocalDate toDate, String expectedResult) throws ServiceOperationException {
    //When
    String result = currencyService.validateDate(fromDate, toDate);

    //Then
    assertEquals(expectedResult, result);
  }

  private static Stream<Arguments> dateArguments() {
    return Stream.of(
        Arguments.of(null, LocalDate.now(), "from date cannot be null"),
        Arguments.of(LocalDate.now(), null, "to date cannot be null"),
        Arguments.of(LocalDate.of(2019, 5, 20), LocalDate.of(2019, 5, 21), null),
        Arguments.of(LocalDate.of(2019, 5, 20), LocalDate.of(2019, 5, 10)
            , "to date cannot be after from date")
    );
  }
}

