package com.practice.springbatch_practice1.config.step.chunk.jdbccursoritemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * TODO sql 파일을 따로 관리 (sql파일 혹은 xml파일로 관리)
 * TODO myBatis는 안되는지 확인.
 * TODO 다중 디비 접속 확인.
 */
@Configuration
public class JdbcCursorItemReaderConfiguration {

    private int chunkSize = 10;
    @Value("classpath:sql/jdbcCursorItemReader.sql")
    private Resource sql;
    private final DataSource dataSource;

    public JdbcCursorItemReaderConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Job jdbcCursorItemReaderJob(JobRepository jobRepository
            , Step jdbcCursorItemReaderStep) {
        return new JobBuilder("jdbcCursorItemReaderJob", jobRepository)
                .start(jdbcCursorItemReaderStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jdbcCursorItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(jdbcCursorItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    private ItemReader<Customer> jdbcCursorItemReaderReader() {
        String sql;
        try(InputStream is = this.sql.getInputStream()) {
            sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReaderReader")
                .fetchSize(chunkSize)
                .sql(sql)
                .queryArguments("A%")
                .dataSource(dataSource)
                .beanRowMapper(Customer.class)
                .build();
    }
}
