package com.practice.springbatch_practice1.config.step.chunk.jpacursoritemreader;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaCursorItemReaderConfiguration {

    private final EntityManagerFactory entityManager;

    public JpaCursorItemReaderConfiguration(EntityManagerFactory entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public Job jpaCursorItemReaderJob(JobRepository jobRepository
    , Step jpaCursorItemReaderStep) {
        return new JobBuilder("jpaCursorItemReaderJob", jobRepository)
                .start(jpaCursorItemReaderStep)
                .build();
    }

    @Bean
    public Step jpaCursorItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jpaCursorItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jpaCursorItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    private ItemReader<Customer> jpaCursorItemReaderReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "A%");

        return new JpaCursorItemReaderBuilder<Customer>()
                .name("jpaCursorItemReaderReader")
                .entityManagerFactory(entityManager)
                .queryString("select c from Customer c where firstName like :firstname")
                .parameterValues(parameters)
                .build();
    }
}
