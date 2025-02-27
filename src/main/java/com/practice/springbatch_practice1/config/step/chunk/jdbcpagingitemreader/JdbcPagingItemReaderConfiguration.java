package com.practice.springbatch_practice1.config.step.chunk.jdbcpagingitemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JdbcPagingItemReaderConfiguration {
    private final DataSource dataSource;

    public JdbcPagingItemReaderConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Job jdbcPagingItemReaderJob(JobRepository jobRepository, Step jdbcPagingItemReaderStep) {
        return new JobBuilder("jdbcPagingItemReaderJob", jobRepository)
                .start(jdbcPagingItemReaderStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("jdbcPagingItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jdbcPagingItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jdbcPagingItemReaderReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingItemReaderReader")
                .dataSource(dataSource)
                .pageSize(10)
                .queryProvider(createQueryProvider())
                .parameterValues(parameters)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryFactory = new SqlPagingQueryProviderFactoryBean();
        queryFactory.setDataSource(dataSource);
        queryFactory.setSelectClause("id, firstName, lastName, birthdate"); // select를 붙여도 안붙여도 된다.
        queryFactory.setFromClause("customer"); // from을 붙여도 안붙여도 된다.
        queryFactory.setWhereClause("firstName like :firstName"); // where를 붙여도 안붙여도 된다.

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("id", Order.ASCENDING);
        queryFactory.setSortKeys(sortKey); // setSortKey는 String형이고 Order.ASCENDING가 기본 옵션으로 달려있다.
        return queryFactory.getObject();
    }
}
