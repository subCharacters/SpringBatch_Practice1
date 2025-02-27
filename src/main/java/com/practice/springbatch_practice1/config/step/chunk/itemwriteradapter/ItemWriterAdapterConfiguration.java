package com.practice.springbatch_practice1.config.step.chunk.itemwriteradapter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ItemWriterAdapterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ItemWriterAdapterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job itemWriterAdapterJob() {
        return new JobBuilder("itemWriterAdapterJob", jobRepository)
                .start(itemWriterAdapterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step itemWriterAdapterStep() {
        return new StepBuilder("itemWriterAdapterStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item" + i;
                    }
                })
                .writer(itemWriterAdapterWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super String> itemWriterAdapterWriter() {
        ItemWriterAdapter itemWriterAdapter = new ItemWriterAdapter<String>();
        itemWriterAdapter.setTargetObject(itemWriterAdapterObject());
        itemWriterAdapter.setTargetMethod("customWriter");

        return itemWriterAdapter;
    }

    @Bean
    public ItemWriterAdapterCustom itemWriterAdapterObject() {
        return new ItemWriterAdapterCustom();
    }

}
