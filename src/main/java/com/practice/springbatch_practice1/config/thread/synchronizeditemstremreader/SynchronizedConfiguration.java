package com.practice.springbatch_practice1.config.thread.synchronizeditemstremreader;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemReaderBuilder;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// 리더는 paging리더들을 제외하고는 동기화 이슈가 있어서 스레드 세이프하게 이용하고 싶을때 사용 가능.
@Configuration
public class SynchronizedConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public SynchronizedConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job synchronizeJob() {
        return new JobBuilder("synchronizeJob", jobRepository)
                .start(synchronizeStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step synchronizeStep() {
        return new StepBuilder("synchronizeStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(synchronizeReader())
                .listener(new ItemReadListener<Customer>() {
                    @Override
                    public void afterRead(Customer item) {
                        System.out.println("Thread.getName() = " + Thread.currentThread().getName() + ", item.getId() = " + item.getId());
                    }
                })
                .writer(synchronizeWriter())
                // .taskExecutor(synchronizedtaskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Customer> synchronizeReader() {
/*
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from CUSTOMER");
        // BeanPropertyRowMapper와 아래와 어느쪽을 사용해도 되는듯하다.
//        reader.setRowMapper(new RowMapper<Customer>() {
//            @Override
//            public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return new Customer(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("birthdate"));
//            }
//        });
        reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
        reader.setFetchSize(10);*/

        JdbcCursorItemReader<Customer> builder = new JdbcCursorItemReaderBuilder<Customer>()
                .dataSource(dataSource)
                .fetchSize(10)
                .beanRowMapper(Customer.class)
                .sql("select * from CUSTOMER")
                .name("customer") // 필수..
                .build();

        return new SynchronizedItemStreamReaderBuilder<Customer>()
                .delegate(builder)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Customer> synchronizeWriter() {
        return new ItemWriter<Customer>() {
            @Override
            public void write(Chunk<? extends Customer> chunk) throws Exception {
                chunk.getItems().forEach(System.out::println);
            }
        };
    }

    @Bean
    public TaskExecutor synchronizedtaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("taskExecutor-");
        return executor;
    }
}
