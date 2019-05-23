package com.aws.codestar.projecttemplates.controller;

import com.aws.codestar.projecttemplates.Model.Currencies;
import com.aws.codestar.projecttemplates.Model.Exchange;
import com.aws.codestar.projecttemplates.Model.Ranges;
import com.aws.codestar.projecttemplates.Service.CurrencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.patriques.output.exchange.data.ForexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/currencyservice", description = "Available operations for currency exchange service", tags = {"CurrencyService"})
@RestController
@RequestMapping("/currencyservice")
@CrossOrigin
public class CurrencyController {

  private static Logger log = LoggerFactory.getLogger(CurrencyController.class);

  private CurrencyService currencyService;

  @Autowired
  public CurrencyController(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  @GetMapping("/from{from}/to{to}")
  @ApiOperation(
      value = "Get rate for given currencies.",
      response = Float.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol from list", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol from list", example = "PLN")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Float.class),
//      @ApiResponse(code = 404, message = "Rate not found for passed from or to.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getRate(@PathVariable("from") Currencies from, @PathVariable("to") Currencies to) {
    try {
      log.debug("Getting rate for currencies: {} - {}", from, to);
      Exchange exchange = currencyService.getRateFromGivenCurrencies(from.getSymbol(), to.getSymbol());
//      if (exchange.getRate() != -1) {
        return new ResponseEntity<>(exchange, HttpStatus.OK);
//      }
//      return new ResponseEntity<>(new ErrorMessage(String.format("Rate not found for passed symbols: %s, %s", from, to)), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting rate for currencies: %s, %s", from, to);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/from{from}/to{to}/ranges{ranges}")
  @ApiOperation(
      value = "Get historical data for given currencies and range.",
      response = ForexData.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol from list", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol from list", example = "PLN"),
      @ApiImplicitParam(name = "ranges", value = "Ranges to get data", example = "Week")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = ForexData.class),
//      @ApiResponse(code = 404, message = "Rate not found for passed from or to.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getHistoricalData(@PathVariable("from") Currencies from, @PathVariable("to") Currencies to, @PathVariable("ranges") Ranges ranges) {
    try {
      log.debug("Getting rate by currencies: {} - {}", from, to);
//      if (currencyService.getRateFromGivenCurrencies(from.getSymbol(), to.getSymbol()).getRate() != 0) {
        List<ForexData> forexDataList= currencyService.getHistoricalDataForGivenCurrenciesAndRange(from.getSymbol(), to.getSymbol(), ranges);
        return new ResponseEntity<>(forexDataList, HttpStatus.OK);
//      }
//      return new ResponseEntity<>(new ErrorMessage(String.format("Rate not found for passed symbols: %s, %s", from, to)), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting rate for currencies: %s, %s", from, to);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
