package com.btb.sne.config;

import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    LoggingMeterRegistry loggingMeterRegistry() {
        LoggingMeterRegistry registry = new LoggingMeterRegistry();
        registry.config()
                .meterFilter(MeterFilter.denyNameStartsWith("jvm"))
                .meterFilter(MeterFilter.denyNameStartsWith("process"))
                .meterFilter(MeterFilter.denyNameStartsWith("system"))
                .meterFilter(MeterFilter.denyNameStartsWith("logback"));
        return registry;
    }
}
