package com.btb.sne.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationConfig {

    @Value("${chunk.size:100}")
    private int chunkSize;

    @Value("${max.count:1000000}")
    private int maxCount;

    @Value("${delete.all:true}")
    private boolean delete;
}
