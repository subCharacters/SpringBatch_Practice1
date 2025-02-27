package com.practice.springbatch_practice1.config.step.chunk.compositeitemprocess;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CompositeItemProcessConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public CompositeItemProcessConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job compositeItemProcessJob() {
        return new JobBuilder("compositeItemProcessJob", jobRepository)
                .start(compositeItemProcessStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step compositeItemProcessStep() {
        return new StepBuilder("compositeItemProcessStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item" + i;
                    }
                })
                .processor(compositeItemProcessProcess())
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> compositeItemProcessProcess() {
        List itemProcessors = new ArrayList<>();
        itemProcessors.add(new CompositeItemProcessor1());
        itemProcessors.add(new CompositeItemProcessor2());

        return new CompositeItemProcessorBuilder<>()
                .delegates(itemProcessors)
                .build();
    }
}
