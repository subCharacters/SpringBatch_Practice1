package com.practice.springbatch_practice1.config.step.chunk.jsonitemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JsonItemReaderConfiguration {

    @Bean
    public Job jsonItemReaderJob(JobRepository jobRepository, Step jsonItemReaderStep) {
        return new JobBuilder("jsonItemReaderJob", jobRepository)
                .start(jsonItemReaderStep)
                .build();
    }

    @Bean
    public Step jsonItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jsonItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(2, transactionManager)
                .reader(jsonItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    private ItemReader<? extends Customer> jsonItemReaderReader() {
        return new JsonItemReaderBuilder<Customer>()
                .name("jsonItemReaderReader")
                .resource(new ClassPathResource("customer.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
                .build();
    }
}
