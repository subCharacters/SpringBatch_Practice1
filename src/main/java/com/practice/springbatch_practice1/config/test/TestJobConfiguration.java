package com.practice.springbatch_practice1.config.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public TestJobConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job testJob() throws Exception {
        return new JobBuilder("testJob", jobRepository)
                .start(testStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step testStep() throws Exception {
        return new StepBuilder("testStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(testReader())
                .writer(testWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer> testReader() throws Exception {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(100);
        reader.setFetchSize(100);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));

        SqlPagingQueryProviderFactoryBean queryFactory = new SqlPagingQueryProviderFactoryBean();
        queryFactory.setDataSource(dataSource);
        queryFactory.setSelectClause("id, firstName, lastName, birthdate");
        queryFactory.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryFactory.setSortKeys(sortKeys);

        reader.setQueryProvider(queryFactory.getObject());
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> testWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("insert into customer3 values(:id, :firstName, :lastName, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();
        return writer;
    }

}
