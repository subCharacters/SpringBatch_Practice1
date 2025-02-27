package com.practice.springbatch_practice1.config.step.chunk.jdbcbatchitemwriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
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
public class JdbcBatchItemWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public JdbcBatchItemWriterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, PagingQueryProvider createQueryProvider) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job jdbcBatchItemWriterJob() throws Exception {
        return new JobBuilder("jdbcBatchItemWriterJob", jobRepository)
                .start(jdbcBatchItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jdbcBatchItemWriterStep() throws Exception {
        return new StepBuilder("jdbcBatchWriterStep", jobRepository)
                .<Customer, Customer2>chunk(10, transactionManager)
                .reader(jdbcBatchItemWriterReader())
                .writer(jdbcBatchItemWriterWriter())
                .build();
    }

    // TODO POJO빈(beanMapped) 형식과 바인딩 형식 이외의 map형식(columnMapped)과 ?(Prepared Statement)형식
    @Bean
    public ItemWriter<? super Customer2> jdbcBatchItemWriterWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .dataSource(dataSource)
                // Read에서 받아오는 빈과 이름만 같으면 알아서 바인딩 해주는듯 하다.
                .sql("insert into customer2 values(:id, :firstName, :lastName)")
                .beanMapped()
                // 트랜잭션 이후에 업데이트 혹은 삭제가 없을 경우 예외 발생 여부. 기본 값은 true
                .assertUpdates(true)
                .build();
    }

    // jdbc paging은 bean으로 안하면 datasource가 null로 되어버리는듯.
    @Bean
    public ItemReader<? extends Customer> jdbcBatchItemWriterReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcBatchWriterReader")
                .dataSource(dataSource)
                .pageSize(10)
                .queryProvider(jdbcBatchWriterQueryProvider())
                .parameterValues(parameters)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public PagingQueryProvider jdbcBatchWriterQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id, firstName, lastName, birthdate");
        factoryBean.setFromClause("customer");
        factoryBean.setWhereClause("firstName like :firstName");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        factoryBean.setSortKeys(sortKeys);
        return factoryBean.getObject();
    }
}
