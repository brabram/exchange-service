package com.aws.codestar.projecttemplates.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.aws.codestar.projecttemplates.Configuration.ApplicationConfiguration;
import com.aws.codestar.projecttemplates.Model.ExchangeData;
import com.aws.codestar.projecttemplates.Model.HistoricalData;
import com.aws.codestar.projecttemplates.Model.SupportedCurrencySymbols;
import com.aws.codestar.projecttemplates.Service.CurrencyService;
import com.aws.codestar.projecttemplates.Validator.ArgumentValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpServerErrorException;
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CurrencyController.class)
@AutoConfigureMockMvc
class CurrencyControllerTest {

  private ObjectMapper mapper = new ApplicationConfiguration().getObjectMapper();
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ArgumentValidator argumentValidator;

  @MockBean
  private SupportedCurrencySymbols supportedCurrencySymbols;

  @MockBean
  private CurrencyService currencyService;

  @Test
  void shouldReturnAllSupportedCurrencies() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    when(currencyService.getSupportedCurrencies()).thenReturn(currencies);

    //When
    MvcResult result = mockMvc
        .perform(get("/getAllCurrencies")
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    Set<String> actualCurrencies = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Set<String>>() {
    });

    //Then
    assertEquals(HttpStatus.OK.value(), actualHttpStatus);
    assertEquals(currencies, actualCurrencies);
    verify(currencyService).getSupportedCurrencies();
  }

  @Test
  void shouldReturnInternalServerErrorDuringGettingAllSupportedCurrenciesWhenSomethingWentWrongOnServer() throws Exception {
    //Given

    doThrow(HttpServerErrorException.InternalServerError.class).when(currencyService).getSupportedCurrencies();
    ErrorMessage expectedResponse = new ErrorMessage("Internal server error while getting all currencies");

    //When
    MvcResult result = mockMvc
        .perform(get("/getAllCurrencies")
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
    verify(currencyService).getSupportedCurrencies();
  }

  @Test
  void shouldReturnRate() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "USD";
    float rate = 12;
    ExchangeData exchange = new ExchangeData(from, to, rate);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(true);
    when(currencyService.getRateFromGivenCurrencies(from, to)).thenReturn(exchange);

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s", from, to))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ExchangeData actualExchange = mapper.readValue(result.getResponse().getContentAsString(), ExchangeData.class);

    //Then
    assertEquals(HttpStatus.OK.value(), actualHttpStatus);
    assertEquals(exchange, actualExchange);
    verify(currencyService).getRateFromGivenCurrencies(from, to);
  }

  @Test
  void shouldReturnBadRequestStatusWhenToSymbolIsNotSupported() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "XYZ";
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(false);
    ErrorMessage message = new ErrorMessage(String.format("Bad request for passed symbol: %s", to));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s", from, to))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualMessage = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
    assertEquals(message, actualMessage);
  }

  @Test
  void shouldReturnNotFoundStatusWhenFromSymbolIsNotSupported() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "XXX";
    String to = "PLN";
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(false);
    ErrorMessage message = new ErrorMessage(String.format("Bad request for passed symbol: %s", from));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s", from, to))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualMessage = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
    assertEquals(message, actualMessage);
  }

  @Test
  void shouldReturnInternalServerErrorDuringGettingRateWhenSomethingWentWrongOnServer() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "EUR";
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(true);
    doThrow(HttpServerErrorException.InternalServerError.class).when(currencyService).getRateFromGivenCurrencies(from, to);
    ErrorMessage expectedResponse = new ErrorMessage(String.format("Internal server error while getting rate for currencies: %s, %s",
        from, to));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s", from, to))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
    verify(currencyService).getRateFromGivenCurrencies(from, to);
  }

  @Test
  void shouldReturnHistoricalData() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "USD";
    LocalDate fromDate = LocalDate.of(2019, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 21);
    List<HistoricalData> historicalDataList = new ArrayList<>();
    HistoricalData historicalData = new HistoricalData(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(0, 0, 0)), 3, 4, 2, 3);
    HistoricalData historicalData1 = new HistoricalData(LocalDateTime.of(LocalDate.of(2019, 5, 21), LocalTime.of(0, 0, 0)), 30, 40, 20, 30);
    historicalDataList.add(historicalData);
    historicalDataList.add(historicalData1);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(true);
    when(argumentValidator.validateDate(fromDate, toDate)).thenReturn(true);
    when(currencyService.getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate)).thenReturn(historicalDataList);

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s/fromDate=%s&toDate=%s", from, to, fromDate, toDate))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    List<HistoricalData> actualHistoricalDataList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<HistoricalData>>() {
    });

    //Then
    assertEquals(HttpStatus.OK.value(), actualHttpStatus);
    assertEquals(historicalDataList, actualHistoricalDataList);
    verify(currencyService).getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate);
  }

  @Test
  void shouldReturnNotFoundStatusWhenFromSymbolIsIncorrect() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "XXX";
    String to = "USD";
    LocalDate fromDate = LocalDate.of(2019, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 21);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(false);
    ErrorMessage expectedResponse = new ErrorMessage(String.format("Bad request for passed symbol: %s", from));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s/fromDate=%s&toDate=%s", from, to, fromDate, toDate))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void shouldReturnNotFoundStatusWhenToSymbolIsIncorrect() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "XXX";
    LocalDate fromDate = LocalDate.of(2019, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 21);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(false);
    ErrorMessage expectedResponse = new ErrorMessage(String.format("Bad request for passed symbol: %s", to));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s/fromDate=%s&toDate=%s", from, to, fromDate, toDate))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void shouldReturnNotFoundStatusWhenFromDateIsIncorrect() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "USD";
    LocalDate fromDate = LocalDate.of(2020, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 21);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(true);
    when(argumentValidator.validateDate(fromDate, toDate)).thenReturn(false);
    ErrorMessage expectedResponse = new ErrorMessage(String.format("Passed dates are incorrect: %s, %s", fromDate, toDate));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s/fromDate=%s&toDate=%s", from, to, fromDate, toDate))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void shouldReturnInternalServerErrorDuringGettingForexDataWhenSomethingWentWrongOnServer() throws Exception {
    //Given
    Set<String> currencies = new HashSet<>();
    currencies.add("PLN");
    currencies.add("EUR");
    currencies.add("USD");
    String from = "PLN";
    String to = "USD";
    LocalDate fromDate = LocalDate.of(2020, 5, 20);
    LocalDate toDate = LocalDate.of(2019, 5, 21);
    when(supportedCurrencySymbols.getSupportedSymbols()).thenReturn(currencies);
    when(argumentValidator.validateSymbol(from)).thenReturn(true);
    when(argumentValidator.validateSymbol(to)).thenReturn(true);
    when(argumentValidator.validateDate(fromDate, toDate)).thenReturn(true);
    doThrow(HttpServerErrorException.InternalServerError.class).when(currencyService)
        .getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate);
    ErrorMessage expectedResponse = new ErrorMessage(String.format("Internal server error while getting forex data for currencies: %s, %s, and dates: %s, %s",
        from, to, fromDate, toDate));

    //When
    MvcResult result = mockMvc
        .perform(get(String.format("/from=%s&to=%s/fromDate=%s&toDate=%s", from, to, fromDate, toDate))
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andReturn();
    int actualHttpStatus = result.getResponse().getStatus();
    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);

    //Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualHttpStatus);
    assertEquals(expectedResponse, actualResponse);
    verify(currencyService).getHistoricalDataForGivenCurrenciesAndRange(from, to, fromDate, toDate);
  }
}
