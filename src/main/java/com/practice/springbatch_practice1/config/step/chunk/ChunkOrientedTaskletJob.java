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
public class ChunkOrientedTaskletJob {

    @Bean
    public Job batchChunkOrientedTaskletJob(JobRepository jobRepository
            , Step chunkOrientedTaskletStep1
            , Step chunkOrientedTaskletStep2) {
        return new JobBuilder("batchChunkOrientedTaskletJob", jobRepository)
                .start(chunkOrientedTaskletStep1)
                .next(chunkOrientedTaskletStep2)
                .build();
    }

    @Bean
    public Step chunkOrientedTaskletStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkOrientedTaskletStep1", jobRepository)
                .<String, String>chunk(2, transactionManager)
                // 값을 읽을때 chunkContext에 데이터를 캐싱하고 write까지 끝나면 삭제한다.
                // 중간에 에러가 발생했을 경우 DB나 파일로부터 재취득하지 않고 캐싱한 값을 사용하기 위해서.
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        Thread.sleep(300);
                        System.out.println("item = " + item);
                        return "my_" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })
                .build();
    }

    @Bean
    public Step chunkOrientedTaskletStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunkOrientedTaskletStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
