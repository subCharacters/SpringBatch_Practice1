package com.practice.springbatch_practice1.config.step.chunk.flatfileitemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlatFileItemReaderConfiguration {

    @Bean
    public Job flatFileItemReaderJob(JobRepository jobRepository
    , Step flatFileItemReaderStep1
    , Step flatFileItemReaderStep2) {
        return new JobBuilder("flatFileItemReaderJob", jobRepository)
                .start(flatFileItemReaderStep1)
                .next(flatFileItemReaderStep2)
                .build();
    }

    @Bean
    public Step flatFileItemReaderStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flatFileItemReaderStep1", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(flatFileItemReaderReader())
                .writer(new ItemWriter() {
                    @Override
                    public void write(Chunk chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();

    }

    @Bean
    public ItemReader flatFileItemReaderReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("/customers.csv"));

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new CustomerFiledSetMapper());

        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(1);

        return reader;
    }

    @Bean
    public Step flatFileItemReaderStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flatFileItemReaderStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was executed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
