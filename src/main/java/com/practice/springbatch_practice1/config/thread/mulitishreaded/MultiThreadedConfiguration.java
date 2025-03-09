package com.practice.springbatch_practice1.config.thread.mulitishreaded;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 각각 Step에서 ItemReader가 실행될때 work에서 chunk를 생성하여 processor writer까지 진행.
 * 스레드들은 데이터를 공유하지 않음.
 * JdbcPagingReader와 JPAPagingReader를 이용하는 것이 스레드 세이프함.
 *
 */
@Configuration
public class MultiThreadedConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public MultiThreadedConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job multiThreadedJob() {
        return new JobBuilder("multiThreadedJob", jobRepository)
                .start(multiThreadedStep())
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {

                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        System.out.println("=============================");
                        System.out.println(Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis());
                        System.out.println("=============================");
                    }
                })
                .build();
    }

    @Bean
    public Step multiThreadedStep() {
        return new StepBuilder("multiThreadedStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(multiThreadedCustomerReader())
                .processor((ItemProcessor<? super Customer, ? extends Customer>) item -> item)
                .writer(multiThreadedCustomerWriter())
                .taskExecutor(multiThreadedTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor multiThreadedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 기본 스레드 갯수
        executor.setMaxPoolSize(20); // 최대 스레드 갯수
        executor.setThreadNamePrefix("multiThreadedTaskExecutor-"); // 이름짓기
        return executor;
    }

    @Bean
    public JdbcPagingItemReader<Customer> multiThreadedCustomerReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(100);
        reader.setRowMapper(new RowMapper<Customer>() {
            @Override
            public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Customer(
                        rs.getInt("id")
                        , rs.getString("firstName")
                        , rs.getString("lastName")
                        , rs.getString("birthdate")
                );
            }
        });

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> multiThreadedCustomerWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("insert into customer3 values(:id, :firstName, :lastName, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();

        return writer;
    }
}
