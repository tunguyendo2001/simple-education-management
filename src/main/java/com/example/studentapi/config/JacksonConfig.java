package com.example.studentapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(new Hibernate5Module(), new JavaTimeModule())
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.disable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        return module;
    }
}