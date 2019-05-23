//package com.aws.codestar.projecttemplates.controller.Controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//import com.aws.codestar.projecttemplates.Configuration.ApplicationConfiguration;
//import com.aws.codestar.projecttemplates.Model.Exchange;
//import com.aws.codestar.projecttemplates.Service.CurrencyService;
//import com.aws.codestar.projecttemplates.controller.CurrencyController;
//import com.aws.codestar.projecttemplates.controller.ErrorMessage;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.patriques.ForeignExchange;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = CurrencyController.class)
//@AutoConfigureMockMvc
//class CurrencyControllerTest {
//
//  private final String urlAddressTemplate = "/currencyservice";
//  private ObjectMapper mapper = new ObjectMapper();
//  private ForeignExchange foreignExchange = new ApplicationConfiguration().getForeignExchange();
//
//  @Autowired
//  MockMvc mockMvc;
//
//  @MockBean
//  private CurrencyService currencyService;
//
//  @Test
//  private void shouldReturnRate() throws Exception {
//    //Given
//    String from = "PLN";
//    String to = "USD";
//    float rate = foreignExchange.currencyExchangeRate(from, to).getData().getExchangeRate();
//    Exchange exchange = new Exchange(from, to, rate);
//    when(currencyService.getRateFromGivenCurrencies(from, to)).thenReturn(exchange);
//
//    //When
//    MvcResult result = mockMvc
//        .perform(get(String.format("%s/from%s/to%s", urlAddressTemplate, from, to))
//            .accept(MediaType.APPLICATION_JSON_UTF8))
//        .andReturn();
//    int actualHttpStatus = result.getResponse().getStatus();
//    Exchange actualExchange = mapper.readValue(result.getResponse().getContentAsString(), Exchange.class);
//
//    //Then
//    assertEquals(HttpStatus.OK.value(), actualHttpStatus);
//    assertEquals(exchange, actualExchange);
//    verify(currencyService).getRateFromGivenCurrencies(from, to);
//  }
//
////  @Test
////  void shouldReturnNotFoundStatusWhenToSymboldoesNotExist() throws Exception {
////    //Given
////    String from = "PLN";
////    String to = "XYZ";
////    float rate = -1;
////    Exchange exchange = new Exchange(from, to, rate);
////    when(currencyService.getRateFromGivenCurrencies(from, to)).thenReturn(exchange);
////
////    //When
////    MvcResult result = mockMvc
////        .perform(get(String.format("%s/from%s/to%s", urlAddressTemplate, from, to))
////            .accept(MediaType.APPLICATION_JSON_UTF8))
////        .andReturn();
////    int actualHttpStatus = result.getResponse().getStatus();
////
////    //Then
////    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
////    verify(currencyService).getRateFromGivenCurrencies(from, to);
////  }
//
////  @Test
////  void shouldReturnNotFoundStatusWhenFromSymboldoesNotExist() throws Exception {
////    //Given
////    String from = "XYZ";
////    String to = "EUR";
////    float rate = -1;
////    Exchange exchange = new Exchange(from, to, rate);
////    when(currencyService.getRateFromGivenCurrencies(from, to)).thenReturn(exchange);
////
////    //When
////    MvcResult result = mockMvc
////        .perform(get(String.format("%s/from%s/to%s", urlAddressTemplate, from, to))
////            .accept(MediaType.APPLICATION_JSON_UTF8))
////        .andReturn();
////    int actualHttpStatus = result.getResponse().getStatus();
////
////    //Then
////    assertEquals(HttpStatus.BAD_REQUEST.value(), actualHttpStatus);
////    verify(currencyService).getRateFromGivenCurrencies(from, to);
////  }
//
//  @Test
//  void shouldReturnInternalServerErrorDuringGettingRateWhenSomethingWentWrongOnServer() throws Exception {
//    //Given
//    String from = "PLN";
//    String to = "EUR";
//    doThrow(Error.class).when(currencyService).getRateFromGivenCurrencies(from, to);
//    ErrorMessage expectedResponse = new ErrorMessage(String.format("Internal server error while getting rate by symbols: %s, %s", from, to));
//
//    //When
//    MvcResult result = mockMvc
//        .perform(get(String.format("%s/from%s/to%s", urlAddressTemplate, from, to))
//            .accept(MediaType.APPLICATION_JSON_UTF8))
//        .andReturn();
//    int actualHttpStatus = result.getResponse().getStatus();
//    ErrorMessage actualResponse = mapper.readValue(result.getResponse().getContentAsString(), ErrorMessage.class);
//
//    //Then
//    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualHttpStatus);
//    assertEquals(expectedResponse, actualResponse);
//    verify(currencyService).getRateFromGivenCurrencies(from, to);
//  }
//}
