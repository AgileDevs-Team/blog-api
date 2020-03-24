package com.netodevel.blog.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper =
                Jackson2ObjectMapperBuilder.json().propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .dateFormat(new StdDateFormat()).serializationInclusion(JsonInclude.Include.NON_ABSENT)
                        .build();
        return objectMapper;
    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
