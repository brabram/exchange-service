package com.aws.codestar.projecttemplates.Model;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "string", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HistoricalDataMapper {

  @Mapping(target = "open", source = "open")
  @Mapping(target = "high", source = "high")
  @Mapping(target = "low", source = "low")
  @Mapping(target = "close", source = "close")
  HistoricalData mapForexData(org.patriques.output.exchange.data.ForexData forexData);
}
