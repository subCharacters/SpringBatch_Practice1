package com.practice.springbatch_practice1.config.step.chunk.flatfileitemwriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

@Configuration
public class FlatFileItemWriterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    public FlatFileItemWriterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job flatFileItemWriterJob() {
        return new JobBuilder("flatFileItemWriterJob", jobRepository)
                .start(flatFileItemWriterStepDelimited())
                .next(flatFileItemWriterStepFormat())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step flatFileItemWriterStepDelimited() {
        return new StepBuilder("flatFileItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(flatFileItemWriterReader())
                .writer(flatFileItemWriterWriterDelimited())
                .build();
    }

    @Bean
    public Step flatFileItemWriterStepFormat() {
        return new StepBuilder("flatFileItemWriterStepFormat", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(flatFileItemWriterReader())
                .writer(flatFileItemWriterWriterFormat())
                .build();
    }

    public ItemReader<? extends Customer> flatFileItemWriterReader() {
        List<Customer> customers = Arrays.asList(new Customer(1, "user1", 20),
                new Customer(2, "user2", 21),
                new Customer(3, "user3", 22));

        ListItemReader listItemReader = new ListItemReader(customers);
        return listItemReader;
    }

    @Bean
    public ItemWriter<? super Customer> flatFileItemWriterWriterDelimited() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriterWriterDelimited")
                .resource(new FileSystemResource(
                        "C:\\Users\\wldns\\IdeaProjects\\SpringBatch_Practice1\\" +
                                "src\\main\\resources\\writer\\flatFileItemWriter.txt")) // 쓰기 할 리소스
                // DelimitedLineAggregator를 내부적으로 사용
                // 파일의 구분자 설정(기본 LineAggregator를 세팅하는 빌더).
                // default는 ,
                .delimited().delimiter(",")
                // BeanWrapperFieldExtractor를 간단히 설정.
                // BeanWrapperFieldExtractor.setNames(...)와 같음
                // 객체를 문자열로 만들때 Customer 클래스의 필드를 참조. 일치 안하면 Invalid property '다른값' of bean class에러남.
                .names(new String[]{"id", "name", "age"})
                .append(true) // true:기존 파일이 존재할 경우 이어쓰기, false:파일을 새로 생성. default는 false
                .headerCallback(new FlatFileHeaderCallback() { // 헤더를 설정. 이미 파일이 존재하여  append의 경우는 헤더는 무시됨
                    @Override
                    public void writeHeader(Writer writer) throws IOException {
                        writer.write("id,name,age");
                    }
                })
                .footerCallback(new FlatFileFooterCallback() { // 푸터를 설정. append의 경우에 계속 추가됨
                    @Override
                    public void writeFooter(Writer writer) throws IOException {
                        writer.write("=== end ===");
                    }
                })
                .shouldDeleteIfEmpty(true) // true:쓸 내용이 없다면 해당 파일을 삭제. false:빈 파일도 남김. default는 false.
                .shouldDeleteIfExists(false) // true:파일이 존재하면 삭제 후 작성. false:삭제 안함(append 설정 처리 방식을 따름)
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFileItemWriterWriterFormat() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriterStepFormat")
                .resource(new FileSystemResource(
                        "C:\\Users\\wldns\\IdeaProjects\\SpringBatch_Practice1\\" +
                                "src\\main\\resources\\writer\\flatFileItemWriter.txt"))
                .append(true)
                .formatted().format("%-2d%-15s%-2d") // 이런 형식으로 해야 됨(규칙) 빈 곳은 스페이스로 자릿수를 채운다.
                .names(new String[]{"id", "name", "age"})
                .build();

    }
}
