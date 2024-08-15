package com.ludigi.webchecker.config;

import com.ludigi.webchecker.element.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
class IdGeneratorProvider {
    @Bean
    IdGenerator idGenerator() {
        return UUID::randomUUID;
    }
}
