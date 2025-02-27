package com.practice.springbatch_practice1.config.step.chunk.flatfileitemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * TODO 파일 두개 동시에 읽는 거
 */
@Configuration
public class FlatFileItemReaderTokenizerConfiguration {

    @Bean
    public Job flatFileItemReaderTokenizerJob(JobRepository jobRepository
    , Step flatFileItemReaderDelimitedStep
    , Step flatFileItemReaderFixedLengthStep) {
        return new JobBuilder("flatFileItemReaderTokenizerJob", jobRepository)
                .start(flatFileItemReaderDelimitedStep)
                .next(flatFileItemReaderFixedLengthStep)
                .build();
    }

    @Bean
    public Step flatFileItemReaderDelimitedStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
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
                .strict(false) // 엄격하게 검사 유무. 기본은 true로 읽은 행의 항목 수와 설정한 항목 수를 체크한다.
                .targetType(Customer.class)
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("name", "age", "year") // 열 방식으로 읽어들이려면 BeanWrapperFieldSetMapper말고 직접 field set mapper를 구현할 필요가 있음.
                .build();
    }

    @Bean
    public Step flatFileItemReaderFixedLengthStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flatFileItemReaderFixedLengthStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(flatFileItemReaderFixedLengthReader())
                .writer(new ItemWriter() {
                    @Override
                    public void write(Chunk chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    @Bean
    public ItemReader flatFileItemReaderFixedLengthReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemReaderFixedLengthReader")
                .resource(new FileSystemResource("C:\\Users\\wldns\\IdeaProjects\\SpringBatch_Practice1\\src\\main\\resources\\customers.txt"))
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                .strict(false) // 엄격하게 검사 유무. 기본은 true로 읽은 행의 길이와 range를 체크한다.
                //.addColumns(new Range(1,5))
                //.addColumns(new Range(6,9))
                //.addColumns(new Range(10,11))
                .columns(new Range(1,5), new Range(6,9), new Range(10,11)) // addColumns와 어느 쪽도 사용 가능.
                .names("name", "year", "age")
                .build();
    }
}
