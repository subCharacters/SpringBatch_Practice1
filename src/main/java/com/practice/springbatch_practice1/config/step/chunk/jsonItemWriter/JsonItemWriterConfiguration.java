package com.practice.springbatch_practice1.config.step.chunk.jsonItemWriter;

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
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JsonItemWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public JsonItemWriterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource, PagingQueryProvider createQueryProvider) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job jsonItemWriterJob() throws Exception {
        return new JobBuilder("jsonItemWriterJob", jobRepository)
                .start(jsonItemWriterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jsonItemWriterStep() throws Exception {
        return new StepBuilder("jsonItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jsonItemWriterReader())
                .writer(jsonItemWriterWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> jsonItemWriterWriter() {
        return new JsonFileItemWriterBuilder<Customer>()
                .name("JsonItemWriterWriter")
                .resource(new FileSystemResource(
                        "C:\\Users\\wldns\\IdeaProjects\\SpringBatch_Practice1\\" +
                                "src\\main\\resources\\writer\\jsonItemWriter.json"))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    // jdbc paging은 bean으로 안하면 datasource가 null로 되어버리는듯.
    @Bean
    public ItemReader<? extends Customer> jsonItemWriterReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jsonItemWriterReader")
                .dataSource(dataSource)
                .pageSize(10)
                .queryProvider(jsonItemWriterQueryProvider())
                .parameterValues(parameters)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public PagingQueryProvider jsonItemWriterQueryProvider() throws Exception {
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
