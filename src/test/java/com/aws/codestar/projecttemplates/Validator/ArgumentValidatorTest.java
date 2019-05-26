package com.aws.codestar.projecttemplates.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.aws.codestar.projecttemplates.Model.SupportedCurrencySymbols;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArgumentValidatorTest {

  @Mock
  private SupportedCurrencySymbols supportedCurrencySymbols;

  @InjectMocks
  private ArgumentValidator argumentValidator;

  @Test
  void shouldThrowIllegalArgumentExceptionWhenArgumentIsNull(){
    assertThrows(IllegalArgumentException.class, () -> argumentValidator.ensureNotNull(null, "name"));
  }

  @Test
  void shouldReturnTrueWhenSymbolIsSupported() throws UnirestException {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);

    //When
    Boolean result = argumentValidator.validateSymbol("PLN");

    //Then
    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenSymbolIsNotSupported() throws UnirestException {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);

    //When
    Boolean result = argumentValidator.validateSymbol("XXX");

    //Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenSymbolIsNull() throws UnirestException {
    //When
    Boolean result = argumentValidator.validateSymbol(null);

    //Then
    assertFalse(result);
  }

  @Test
  void shouldReturnTrueWhenFromDateAndToDateAreProperly(){
    //Given
    LocalDate fromDate = LocalDate.now().minusDays(1);
    LocalDate toDate = LocalDate.now();

    //When
    Boolean result = argumentValidator.validateDate(fromDate, toDate);

    //Then
    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenFromDateIsNull(){
    //Given
    LocalDate toDate = LocalDate.now();

    //When
    Boolean result = argumentValidator.validateDate(null, toDate);

    //Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenToDateIsNull(){
    //Given
    LocalDate fromDate = LocalDate.now();

    //When
    Boolean result = argumentValidator.validateDate(fromDate, null);

    //Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenToDateIsBeforeFromDate(){
    //Given
    LocalDate fromDate = LocalDate.now();
    LocalDate toDate = LocalDate.now().minusDays(20);

    //When
    Boolean result = argumentValidator.validateDate(fromDate, toDate);

    //Then
    assertFalse(result);
  }
}
