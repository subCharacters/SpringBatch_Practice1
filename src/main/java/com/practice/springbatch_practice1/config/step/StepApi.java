package com.practice.springbatch_practice1.config.step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepChunkConfiguration {

    @Bean
    public Job stepChunkConfiguration() {
        return new StepBuilder("stepChunkConfiguration")
                .chunk()
                .build();
    }


}
