package com.aws.codestar.projecttemplates.Configuration;

import com.aws.codestar.projecttemplates.Model.ForexDataMapper;
import com.aws.codestar.projecttemplates.Model.ForexDataMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.patriques.AlphaVantageConnector;
import org.patriques.ForeignExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

  @Bean
  @Primary
  public ObjectMapper getObjectMapper(){
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }
}
