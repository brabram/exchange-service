package com.aws.codestar.projecttemplates.Model;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CurrencyExchangeDataMapper {

  @Mapping(target = "from", source = "fromCurrencyCode")
  @Mapping(target = "to", source = "toCurrencyCode")
  @Mapping(target = "rate", source = "exchangeRate")
  CurrencyExchangeData mapExchange (org.patriques.output.exchange.data.CurrencyExchangeData currencyExchangeData);
}
