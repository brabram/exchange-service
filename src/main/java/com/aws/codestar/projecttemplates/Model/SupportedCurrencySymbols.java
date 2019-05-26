package com.aws.codestar.projecttemplates.Model;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Set;
import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource("classpath:application.properties")
public class SupportedCurrencySymbols {

  //  @Value( "${source.http}" )
//  private String currenciesSource = "source.http";
  private String currenciesSource = "https://openexchangerates.org/api/currencies.json";

  public Set<String> getSupportedSymbols() throws UnirestException {
    HttpResponse<JsonNode> jsonResponse = Unirest.get(currenciesSource)
        .asJson();
    return jsonResponse.getBody().getObject().keySet();
  }
}
