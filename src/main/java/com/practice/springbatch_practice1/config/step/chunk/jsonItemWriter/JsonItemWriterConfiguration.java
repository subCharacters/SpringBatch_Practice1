package com.practice.springbatch_practice1.config.step.chunk.jsonItemWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class StaxEventItemWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public StaxEventItemWriterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job staxEventItemWriterJob() {
        return new JobBuilder("staxEventItemWriterJob", jobRepository)
                .start(staxEventItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step staxEventItemWriterStep() {
        return new StepBuilder("staxEventItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(staxEventItemWriterReader())
                .writer(staxEventItemWriterWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> staxEventItemWriterWriter() {
        return new StaxEventItemWriterBuilder<Customer>()
                .name("staxEventItemWriterWriter")
                .resource(new FileSystemResource(
                        "C:\\Users\\wldns\\IdeaProjects\\SpringBatch_Practice1\\" +
                                "src\\main\\resources\\writer\\staxEventItemWriter.xml"))
                .marshaller(itemMarshaller())
                .rootTagName("customers")
                .build();
    }

    private Marshaller itemMarshaller() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("customer", Customer.class);
        map.put("id", Long.class);
        map.put("name", String.class);
        map.put("age", Integer.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(map);
        return xStreamMarshaller;
    }

    private ItemReader<? extends Customer> staxEventItemWriterReader() {
        List<Customer> customers = Arrays.asList(
                new Customer(1, "user1", 21),
                new Customer(2, "user2", 22),
                new Customer(3, "user3", 23),
                new Customer(4, "user4", 24),
                new Customer(5, "user5", 25)
        );

        ListItemReader itemReader = new ListItemReader(customers);
        return itemReader;
    }

}
