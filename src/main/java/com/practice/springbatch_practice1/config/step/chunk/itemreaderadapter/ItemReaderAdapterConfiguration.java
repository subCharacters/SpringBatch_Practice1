package com.practice.springbatch_practice1.config.step.chunk.itemreaderadapter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ItemReaderAdapterConfiguration {

    @Bean
    public Job itemReaderAdapterJob(JobRepository jobRepository, Step itemReaderAdapterStep) {
        return new JobBuilder("itemReaderAdapterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(itemReaderAdapterStep)
                .build();
    }

    @Bean
    public Step itemReaderAdapterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("itemReaderAdapterStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(itemReaderAdapterReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    @Bean
    public ItemReader<String> itemReaderAdapterReader() {
        ItemReaderAdapter<String> itemReaderAdapter = new ItemReaderAdapter();
        itemReaderAdapter.setTargetObject(itemReaderAdapterService());
        itemReaderAdapter.setTargetMethod("customRead");
        return itemReaderAdapter;
    }

    @Bean
    public Object itemReaderAdapterService() {
        return new ItemReaderAdapterService();
    }
}
