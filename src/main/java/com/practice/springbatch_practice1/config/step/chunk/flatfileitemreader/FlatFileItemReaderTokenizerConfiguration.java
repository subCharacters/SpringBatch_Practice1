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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlatFileItemReaderDelimitedConfiguration {

    @Bean
    public Job flatFileItemReaderDelimitedJob(JobRepository jobRepository
    , Step flatFileItemReaderDelimitedStep1
    , Step flatFileItemReaderDelimitedStep2) {
        return new JobBuilder("flatFileItemReaderDelimitedJob", jobRepository)
                .start(flatFileItemReaderDelimitedStep1)
                .next(flatFileItemReaderDelimitedStep2)
                .build();
    }

    @Bean
    public Step flatFileItemReaderDelimitedStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flatFileItemReaderDelimitedStep1", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(flatFileItemReaderDelimitedReader())
                .writer(new ItemWriter() {
                    @Override
                    public void write(Chunk chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();

    }

    // delimetedlinetokenizer 구분자 방식
    @Bean
    public ItemReader flatFileItemReaderDelimitedReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemReaderDelimitedReader")
                /*
                 * new FileSystemResource("")
                 * new UrlResource("")
                 * new InputStreamResource(inputStream)
                 * new ByteArrayResource(byteArray)
                 * 등도 사용가능하다함.
                 */
                .resource(new ClassPathResource("/customers.csv"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("name", "age", "year") // 열 방식으로 읽어들이려면 BeanWrapperFieldSetMapper말고 직접 field set mapper를 구현할 필요가 있음.
                .build();
    }

    @Bean
    public Step flatFileItemReaderDelimitedStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flatFileItemReaderDelimitedStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was executed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
