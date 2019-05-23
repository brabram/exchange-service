package com.aws.codestar.projecttemplates.Configuration;

import org.patriques.AlphaVantageConnector;
import org.patriques.ForeignExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

  private final String apiKey = "api.key";
  private int timeout = 3000;

  @Bean
  public ForeignExchange getForeignExchange() {
    return new ForeignExchange(new AlphaVantageConnector(apiKey, timeout));
  }
}
