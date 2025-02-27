package com.practice.springbatch_practice1.config.step.chunk.jpaitemwriter;

import com.practice.springbatch_practice1.config.step.chunk.jpacursoritemreader.Customer;
import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaItemWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public JpaItemWriterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job jpaItemWriterJob() {
        return new JobBuilder("jpaItemWriterJob", jobRepository)
                .start(jpaItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() {
        return new StepBuilder("jpaItemWriterStep", jobRepository)
                .<Customer, Customer2>chunk(10, transactionManager)
                .reader(jpaItemWriteReader())
                .processor(new ItemProcessor<Customer, Customer2>() {
                    ModelMapper modelMapper = new ModelMapper();
                    @Override
                    public Customer2 process(Customer item) throws Exception {
                        /*Customer2 map = new Customer2();
                        map.setId(item.getId());
                        map.setFirstName(item.getFirstName());
                        map.setLastName(item.getLastName());*/
                        Customer2 map = modelMapper.map(item, Customer2.class);
                        return map;
                    }
                })
                .writer(jpaItemWriterWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jpaItemWriteReader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("jpaItemWriteReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c where firstName like :firstName")
                .pageSize(10)
                .parameterValues(new HashMap<String, Object>(Map.of("firstName", "A%")))
                .build();
    }

    @Bean
    public ItemWriter<? super Customer2> jpaItemWriterWriter() {
        return new JpaItemWriterBuilder<Customer2>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true) // default true
                .build();
    }

}
