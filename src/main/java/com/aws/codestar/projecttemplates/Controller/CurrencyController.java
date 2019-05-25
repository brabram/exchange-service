package com.aws.codestar.projecttemplates.Controller;

import com.aws.codestar.projecttemplates.Model.CurrencyExchangeData;
import com.aws.codestar.projecttemplates.Model.ForexData;
import com.aws.codestar.projecttemplates.Service.CurrencyService;
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

@Api(value = "/", description = "Available operations for currency exchange service", tags = {"CurrencyService"})
@RestController
@RequestMapping("/")
@CrossOrigin
public class CurrencyController {

  private static Logger log = LoggerFactory.getLogger(CurrencyController.class);
  private CurrencyService currencyService;

  @Autowired
  public CurrencyController(CurrencyService currencyService) {
    this.currencyService = currencyService;
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

  @GetMapping("from={from}/to={to}")
  @ApiOperation(
      value = "Get rate for given currencies.",
      response = CurrencyExchangeData.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol (only capital leteres)", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol (only capital leteres)", example = "PLN")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = CurrencyExchangeData.class),
      @ApiResponse(code = 404, message = "Not found passed 'from' or 'to' symbol.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getRate(@PathVariable("from") String from, @PathVariable("to") String to) {
    try {
      log.debug("Getting rate for currencies: {} - {}", from, to);
      if (!currencyService.validateSymbol(from)) {
        String message = String.format("Not found passed symbol: %s", from);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.NOT_FOUND);
      }
      if (!currencyService.validateSymbol(to)) {
        String message = String.format("Not found passed symbol: %s", to);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.NOT_FOUND);
      }
      CurrencyExchangeData exchange = currencyService.getRateFromGivenCurrencies(from, to);
      return new ResponseEntity<>(exchange, HttpStatus.OK);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting rate for currencies: %s, %s", from, to);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("from={from}/to={to}/fromDate={fromDate}/toDate={toDate}")
  @ApiOperation(
      value = "Get historical data for given currencies and range.",
      response = ForexData.class)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "from", value = "Currency symbol (only capital leteres)", example = "EUR"),
      @ApiImplicitParam(name = "to", value = "Currency symbol (only capital leteres)", example = "PLN"),
      @ApiImplicitParam(name = "fromDate", value = "date in format YYYY-MM-DD", example = "2019-05-20"),
      @ApiImplicitParam(name = "toDate", value = "date in format YYYY-MM-DD", example = "2019-05-26")})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = ForexData.class),
      @ApiResponse(code = 404, message = "Not found passed 'from' or 'to' symbol.", response = ErrorMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ErrorMessage.class)})
  public ResponseEntity<?> getHistoricalData(
      @PathVariable("from") String from,
      @PathVariable("to") String to,
      @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @PathVariable("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    try {
      log.debug("Getting forex data for symbols: from: {} - to: {}, and dates: fromDate: {} - toDate: {} ",
          from, to, fromDate, toDate);
      if (!currencyService.validateSymbol(from)) {
        String message = String.format("Not found passed symbol: %s", from);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.NOT_FOUND);
      }
      if (!currencyService.validateSymbol(to)) {
        String message = String.format("Not found passed symbol: %s", to);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.NOT_FOUND);
      }
      if (currencyService.validateDate(fromDate, toDate) != null){
        String message = String.format("Passed dates are incorrect: %s, %s", fromDate, toDate);
        log.debug(message);
        return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.NOT_FOUND);
      }
      List<ForexData> forexDataList =
          currencyService.getHistoricalDataForGivenCurrenciesAndRange(
              from, to, fromDate, toDate);
      return new ResponseEntity<>(forexDataList, HttpStatus.OK);
    } catch (Exception e) {
      String message = String.format("Internal server error while getting forex data for currencies: %s, %s, and dates: %s, %s",
          from, to, fromDate, toDate);
      log.error(message, e);
      return new ResponseEntity<>(new ErrorMessage(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
