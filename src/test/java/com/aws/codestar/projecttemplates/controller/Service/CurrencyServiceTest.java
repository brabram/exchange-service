//
//package com.aws.codestar.projecttemplates.controller.Service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.aws.codestar.projecttemplates.Configuration.ApplicationConfiguration;
//import com.aws.codestar.projecttemplates.Model.Exchange;
//import com.aws.codestar.projecttemplates.Service.CurrencyService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.patriques.ForeignExchange;
//import org.patriques.output.exchange.CurrencyExchange;
//import org.patriques.output.exchange.data.CurrencyExchangeData;
//
//@ExtendWith(MockitoExtension.class)
//class CurrencyServiceTest {
//
//  @Mock
//  private ForeignExchange foreignExchange;
//  @Mock
//  private CurrencyExchange currencyExchange;
//  @Mock
//  private CurrencyExchangeData currencyExchangeData;
//
//  @InjectMocks
//  private CurrencyService currencyService;
//
//  @Test
//  void shouldGetRate() {
//    //Given
//    String from = "USD";
//    String to = "PLN";
//    float rate = getRateFromAPI(from, to);
//    Exchange exchange = new Exchange(from, to, rate);
//    when(currencyExchangeData.getExchangeRate()).thenReturn(rate);
//    when(currencyExchange.getData()).thenReturn(currencyExchangeData);
//    when(foreignExchange.currencyExchangeRate(from, to)).thenReturn(currencyExchange);
//
//    //When
//    Exchange actualExchange = currencyService.getRateFromGivenCurrencies(from, to);
//
//    //Then
//    assertEquals(exchange, actualExchange);
//    verify(foreignExchange).currencyExchangeRate(from, to);
//    verify(currencyExchange).getData();
//    verify(currencyExchangeData).getExchangeRate();
//  }
//
////  @Test
////  void shouldReturnMinusOneWhenFromSymbolIsWrong() {
////    //Given
////    String from = "AAA";
////    String to = "PLN";
////    float rate = -1;
////
////    //When
////    float actualRate = currencyService.getRateFromGivenCurrencies(from, to).getRate();
////
////    //Then
////    assertEquals(rate, actualRate);
////  }
//
////  @Test
////  void shouldReturnMinusOneWhenToSymbolIsWrong() {
////    //Given
////    String from = "PLN";
////    String to = "AAA";
////    float rate = -1;
////
////    //When
////    float actualRate = currencyService.getRateFromGivenCurrencies(from, to).getRate();
////
////    //Then
////    assertEquals(rate, actualRate);
////  }
//
//  private float getRateFromAPI(String from, String to) {
//    ForeignExchange foreignExchange = new ApplicationConfiguration().getForeignExchange();
//    return foreignExchange.currencyExchangeRate(from, to).getData().getExchangeRate();
//  }
//
//}
//
