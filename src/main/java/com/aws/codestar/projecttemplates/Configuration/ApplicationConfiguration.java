package com.aws.codestar.projecttemplates.Configuration;

import com.aws.codestar.projecttemplates.Model.HistoricalDataMapper;
import com.aws.codestar.projecttemplates.Model.HistoricalDataMapperImpl;
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

  @Bean
  public ForeignExchange getForeignExchange() {
    String apiKey = "api.key";
    int timeout = 3000;
    return new ForeignExchange(new AlphaVantageConnector(apiKey, timeout));
  }

  @Bean
  public HistoricalDataMapper getForexDataMapper(){
    return new HistoricalDataMapperImpl();
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
