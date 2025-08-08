package com.bank.mortgage.portal.component;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Increase max depth to something higher (e.g., 2000)
        mapper.getFactory()
                .setStreamWriteConstraints(
                        StreamWriteConstraints.builder().maxNestingDepth(2000).build()
                );

        return mapper;
    }
}
