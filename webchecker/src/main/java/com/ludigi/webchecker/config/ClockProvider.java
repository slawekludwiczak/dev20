package com.ludigi.webchecker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ClockProvider {
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
