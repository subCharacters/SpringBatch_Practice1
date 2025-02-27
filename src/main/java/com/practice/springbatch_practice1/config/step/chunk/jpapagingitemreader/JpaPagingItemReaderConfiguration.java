package com.practice.springbatch_practice1.config.step.chunk.jpapagingitemreader;

import com.practice.springbatch_practice1.config.step.chunk.jpacursoritemreader.Customer;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaPagingItemReaderConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    public JpaPagingItemReaderConfiguration(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job jpaPagingItemReaderJob(JobRepository jobRepository, Step jpaPagingItemReaderStep) {
        return new JobBuilder("jpaPagingItemReaderJob", jobRepository)
                .start(jpaPagingItemReaderStep)
                .build();

    }

    @Bean
    public Step jpaPagingItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jpaPagingItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jpaPagingItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    // TODO query provider
    @Bean
    public ItemReader<? extends Customer> jpaPagingItemReaderReader() {
        Map<String, Object> params = new HashMap<>();
        params.put("firstName", "A%");

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("jpaPagingItemReaderReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c where firstName like :firstName")
                .pageSize(10)
                .parameterValues(params)
                .build();
    }
}
