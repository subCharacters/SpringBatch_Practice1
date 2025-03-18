package com.practice.springbatch_practice1.config.listener.retrylistener;

import com.practice.springbatch_practice1.config.listener.skiplistener.LinkedListItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RetryListenerConfiguration {

    private int itemProcessCnt = 0;
    private int itemWriterCnt = 0;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public RetryListenerConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job retryListenerJob() {
        return new JobBuilder("retryListenerJob", jobRepository)
                .start(retryListenerStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step retryListenerStep() {
        return new StepBuilder("retryListenerStep", jobRepository)
                .<Integer, String>chunk(10, transactionManager)
                .reader(retryListenerReader())
                .processor(new ItemProcessor<Integer, String>() {
                    @Override
                    public String process(Integer item) throws Exception {
                        if (itemProcessCnt < 2) {
                            if (itemProcessCnt % 2 == 0) {
                                itemProcessCnt++;
                            } else if (itemProcessCnt % 2 == 1) {
                                itemProcessCnt++;
                                throw new CustomRetryException("process exception");
                            }
                        }
                        System.out.println("itemProcess: " + item);
                        return String.valueOf(item);
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.getItems().forEach(item -> {
                            if (itemWriterCnt < 2) {
                                if (itemWriterCnt % 2 == 0) {
                                    itemWriterCnt++;
                                } else if (itemWriterCnt % 2 == 1) {
                                    itemWriterCnt++;
                                    throw new CustomRetryException("write exception");
                                }
                            }
                            System.out.println("write : " + item);
                        });
                    }
                })
                .faultTolerant()
                .retryLimit(3)
                .retry(CustomRetryException.class)
                .listener(new CustomRetryListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> retryListenerReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        return new ListItemReader<>(list);
    }
}
