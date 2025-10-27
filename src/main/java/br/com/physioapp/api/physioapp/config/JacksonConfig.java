package br.com.physioapp.api.physioapp.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Module hibernate6Module() {
    Hibernate6Module module = new Hibernate6Module();

    module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);

    module.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);

    return module;
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      builder.modules(new JavaTimeModule());
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    };
  }
}
