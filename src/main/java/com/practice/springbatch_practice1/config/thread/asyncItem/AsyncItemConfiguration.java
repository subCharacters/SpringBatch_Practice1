package com.practice.springbatch_practice1.config.thread.asyncItem;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AsyncItemConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public AsyncItemConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job asyncItemJob() throws InterruptedException {
        return new JobBuilder("asyncItemJob", jobRepository)
                .start(asyncItemStep())
                .incrementer(new RunIdIncrementer())
                .build();

    }

    @Bean
    public Step asyncItemStep() throws InterruptedException {
        return new StepBuilder("asyncItemStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(asyncItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer> asyncItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<Customer>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(300);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id,firstName,lastName,birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    /**
     * @Bean
     * public TaskExecutor taskExecutor() 메소드에서
     * TreadPoolTaskExecutor 를 생성하여
     * setCorePoolSize - 최소 스레드
     * setMaxPoolSize - 최대 스레드
     * setQueueCapacity - 대기 큐 크기
     * initialize 하여 직접 설정하면 스레드로 인한 cpu 과사용을 예방할 수 있다.
     * SpringBatch에서 제공하는 SimpleAsyncTaskExecutor 를 사용하면 processor가 불릴때마다.
     * 무제한으로 생성된다.  즉, 청크사이즈만큼 생성이된다.
     * 1만개의 청크사이즈라고 한다면 스레드 1만개는 많은 양이기에 주의가 필요하다.
     */
    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws InterruptedException {
        AsyncItemProcessor<Customer, Customer> processor = new AsyncItemProcessor();
        processor.setDelegate(asyncCustomItemProcessor());
        processor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return processor;
    }

    public ItemProcessor<Customer, Customer> asyncCustomItemProcessor() throws InterruptedException {
        Thread.sleep(10);

        return new ItemProcessor<Customer, Customer>() {

            @Override
            public Customer process(Customer item) throws Exception {
                return new Customer(item.getId()
                        , item.getFirstName().toUpperCase()
                        , item.getLastName().toUpperCase()
                        , item.getBirthdate());
            }
        };
    }

    @Bean
    public AsyncItemWriter asyncItemWriter() {
        AsyncItemWriter<Customer> writer = new AsyncItemWriter<Customer>();
        writer.setDelegate(asyncCustomItemWriter());
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter asyncCustomItemWriter() {
        JdbcBatchItemWriter writer = new JdbcBatchItemWriter();
        writer.setDataSource(dataSource);
        writer.setSql("insert into customer3(id,firstName,lastName,birthdate) values(:id,:firstName,:lastName,:birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        writer.afterPropertiesSet();
        return writer;
    }

}
