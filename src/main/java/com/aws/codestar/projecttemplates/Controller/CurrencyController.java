package com.aws.codestar.projecttemplates.Controller;

import com.aws.codestar.projecttemplates.Model.ExchangeData;
import com.aws.codestar.projecttemplates.Model.HistoricalData;
import com.aws.codestar.projecttemplates.Service.CurrencyService;
import com.aws.codestar.projecttemplates.Validator.ArgumentValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

@Api(value = "/", tags = {"CurrencyService"})
@RestController
@RequestMapping("/")
@CrossOrigin
public class CurrencyController {

  private static Logger log = LoggerFactory.getLogger(CurrencyController.class);
  private CurrencyService currencyService;
  private ArgumentValidator argumentValidator;

  @Autowired
  public CurrencyController(CurrencyService currencyService, ArgumentValidator argumentValidator) {
    this.currencyService = currencyService;
    this.argumentValidator = argumentValidator;
  }

  @GetMapping("getAllCurrencies")
  @ApiOperation(
      value = "Get all supported currencies.",
      response = Json.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getAllCurrencies() {
    try {
      log.debug("Getting all currencies");
      Set<String> currencies = currencyService.getSupportedCurrencies();
      return new ResponseEntity<>(currencies, HttpStatus.OK);
    } catch (Exception e) {
      String message = "Internal server error while getting all currencies";
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("from={from}&to={to}")
  @ApiOperation(
      value = "Get rate for given currencies.",
      response = ExchangeData.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol (only capital leteres)", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol (only capital leteres)", example = "PLN")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = ExchangeData.class),
      @ApiResponse(code = 400, message = "Bad request for 'from' or 'to' symbol.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getRate(@PathVariable("from") String from, @PathVariable("to") String to) {
    try {
      log.debug("Getting rate for currencies: {} - {}", from, to);
      if (!argumentValidator.validateSymbol(from)) {
        String message = String.format("Bad request for passed symbol: %s", from);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.BAD_REQUEST);
      }
      if (!argumentValidator.validateSymbol(to)) {
        String message = String.format("Bad request for passed symbol: %s", to);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.BAD_REQUEST);
      }
      ExchangeData exchange = currencyService.getRateFromGivenCurrencies(from, to);
      return new ResponseEntity<>(exchange, HttpStatus.OK);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting rate for currencies: %s, %s", from, to);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("from={from}&to={to}/fromDate={fromDate}&toDate={toDate}")
  @ApiOperation(
      value = "Get historical data for given currencies and range.",
      response = HistoricalData.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol (only capital leteres)", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol (only capital leteres)", example = "PLN"),
      @ApiImplicitParam(name = "fromDate", value = "date in format YYYY-MM-DD", example = "2019-05-20"),
      @ApiImplicitParam(name = "toDate", value = "date in format YYYY-MM-DD", example = "2019-05-26")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = HistoricalData.class),
      @ApiResponse(code = 400, message = "Bad request for passed 'from' or 'to' symbol.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getHistoricalData(
      @PathVariable("from") String from,
      @PathVariable("to") String to,
      @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @PathVariable("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    try {
      log.debug("Getting forex data for symbols: from: {} - to: {}, and dates: fromDate: {} - toDate: {} ",
          from, to, fromDate, toDate);
      if (!argumentValidator.validateSymbol(from)) {
        String message = String.format("Bad request for passed symbol: %s", from);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.BAD_REQUEST);
      }
      if (!argumentValidator.validateSymbol(to)) {
        String message = String.format("Bad request for passed symbol: %s", to);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.BAD_REQUEST);
      }
      if (!argumentValidator.validateDate(fromDate, toDate)) {
        String message = String.format("Passed dates are incorrect: %s, %s", fromDate, toDate);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.BAD_REQUEST);
      }
      List<HistoricalData> historicalDataList =
          currencyService.getHistoricalDataForGivenCurrenciesAndRange(
              from, to, fromDate, toDate);
      return new ResponseEntity<>(historicalDataList, HttpStatus.OK);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting forex data for currencies: %s, %s, and dates: %s, %s",
          from, to, fromDate, toDate);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
