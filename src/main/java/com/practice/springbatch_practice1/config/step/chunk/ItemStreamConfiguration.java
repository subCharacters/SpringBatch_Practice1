package com.practice.springbatch_practice1.config.step.chunk;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ItemStreamConfiguration {

    @Bean
    public Job itemStreamJob(JobRepository jobRepository
            , Step itemStreamStep1
            , Step itemStreamStep2) {
        return new JobBuilder("itemStreamJob", jobRepository)
                .start(itemStreamStep1)
                .next(itemStreamStep2)
                .build();
    }

    @Bean
    public Step itemStreamStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("itemStreamStep1", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(itemStreamReader())
                .writer(itemStreamWriter())
                .build();

    }

    @Bean
    @StepScope // 다른 잡도 영향이 있으므로 스코프 설정
    public CustomItemStreamReader itemStreamReader() {
        List<String> items = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            items.add("item" + i);
        }

        return new CustomItemStreamReader(items);
    }

    @Bean
    @StepScope // 다른 잡도 영향이 있으므로 스코프 설정
    public ItemWriter<? super String> itemStreamWriter() {
        return new CustomItemStreamWriter();
    }


    @Bean
    public Step itemStreamStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("itemStreamStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("step2 was executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
