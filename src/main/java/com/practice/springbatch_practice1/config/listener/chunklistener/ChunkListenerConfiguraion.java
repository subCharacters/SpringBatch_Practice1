package com.practice.springbatch_practice1.config.listener.chunklistener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
public class ChunkListenerConfiguraion {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ChunkListenerConfiguraion(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job chunkListenerJob() {
        return new JobBuilder("chunkListenerJob", jobRepository)
                .start(chunkListenerStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    // chunk는 청크시작 전 후에 불러진다.
    // item reader와 item process는 각각의 아이템들이 처리되는 전 후에 하나씩 불러진다.
    // item wirter에 경우는 작업 전 후 에 불러진다.
    // 전부 완료가 되면 청크로 다시 되돌아가고 리더가 없으면 청크를 빠져나온다.
    @Bean
    public Step chunkListenerStep() {
        return new StepBuilder("chunkListenerStep", jobRepository)
                .<Integer, String>chunk(5, transactionManager)
                .listener(new ChunkListener())
                .reader(chunkListenerReader())
                .listener(new ItemReaderListener())
                .processor(chunkListenerProcessor())
                .listener(new ItemProcessorListener())
                .writer(chunkListenerWriter())
                .listener(new ItemWriterListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> chunkListenerReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return new ListItemReader<>(list);
    }

    @Bean
    public ItemProcessor<? super Integer, String> chunkListenerProcessor() {
        return item -> "item" + item;
    }

    @Bean
    public ItemWriter<? super String> chunkListenerWriter() {
        return items -> items.forEach(System.out::println);
    }
}
