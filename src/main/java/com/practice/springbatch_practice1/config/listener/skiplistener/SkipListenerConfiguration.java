package com.practice.springbatch_practice1.config.listener.skiplistener;

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
public class SkipListenerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public SkipListenerConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job skipListenerJob() {
        return new JobBuilder("skipListenerJob", jobRepository)
                .start(skipListenerStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step skipListenerStep() {
        return new StepBuilder("skipListenerStep", jobRepository)
                .<Integer, String>chunk(10, transactionManager)
                .reader(skipListenerReader())
                .processor(new ItemProcessor<Integer, String>() {
                    @Override
                    public String process(Integer item) throws Exception {
                        if (item == 4) {
                            throw new CustomSkipException("Skipping 4 items");
                        }
                        System.out.println("process : " + item);
                        return "item" + item;
                    }
                })
                .writer(new ItemWriter<String>() {

                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.getItems().forEach( item -> {
                            if (item.equals("item5")) {
                                throw new CustomSkipException("Skipping 5 items");
                            }
                            System.out.println("write : " + item);
                        });
                    }
                })
                .faultTolerant()
                .skipLimit(3)
                .skip(CustomSkipException.class)
                .listener(new CustomSkipListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> skipListenerReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return new LinkedListItemReader<>(list);
    }
}