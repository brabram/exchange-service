package com.aws.codestar.projecttemplates.Configuration;

import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexDataMapperImpl;
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
  @Bean
  public ForexDataMapper getForexDataMapper(){
    return new ForexDataMapperImpl();
  }
}
