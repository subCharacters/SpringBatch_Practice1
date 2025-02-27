package com.practice.springbatch_practice1.config.step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
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
public class StepApi {

    @Bean
    public Job batchStepApi(JobRepository jobRepository, Step taskStep, Step chunkStep) {
        return new JobBuilder("batchStepApi", jobRepository)
                .start(taskStep)
                .next(chunkStep)
                .build();
    }

    @Bean
    public Step taskStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("taskStep", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1","item2","item3","item4","item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return item.toUpperCase();
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        System.out.println("do write");
                        chunk.forEach(System.out::println);
                    }
                })
                .build();
    }


    public Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            System.out.println("tasklet");
            return RepeatStatus.FINISHED;
        });
    }
}
