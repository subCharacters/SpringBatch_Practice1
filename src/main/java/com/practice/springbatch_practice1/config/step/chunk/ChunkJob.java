package com.practice.springbatch_practice1.config.step.chunk;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
public class ChunkJob {

    @Bean
    public Job batchChunkJob(JobRepository jobRepository
            , Step chunkJobStep1
            , Step chunkJobStep2) {
        return new JobBuilder("batchChunkJob", jobRepository)
                .start(chunkJobStep1)
                .next(chunkJobStep2)
                .build();
    }

    @Bean
    public Step chunkJobStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkJobStep1", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        Thread.sleep(300);
                        System.out.println("item = " + item);
                        return "my" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        Thread.sleep(300);
                        System.out.println("chunk = " + chunk);
                    }
                })
                .build();
    }

    @Bean
    public Step chunkJobStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkJobStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
