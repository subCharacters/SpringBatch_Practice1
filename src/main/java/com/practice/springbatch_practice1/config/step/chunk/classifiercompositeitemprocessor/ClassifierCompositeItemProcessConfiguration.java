package com.practice.springbatch_practice1.config.step.chunk.classifiercompositeitemprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClassifierCompositeItemProcessConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ClassifierCompositeItemProcessConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job classifierCompositeItemProcessJob() {
        return new JobBuilder("classifierCompositeItemProcessJob", jobRepository)
                .start(classifierCompositeItemProcessStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step classifierCompositeItemProcessStep() {
        return new StepBuilder("classifierCompositeItemProcessStep", jobRepository)
                .<ProcessorInfo, ProcessorInfo>chunk(10, transactionManager)
                .reader(new ItemReader<ProcessorInfo>() {
                    int i = 0;
                    @Override
                    public ProcessorInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        ProcessorInfo processorInfo = ProcessorInfo.builder().id(i).build();
                        return i > 3 ? null : processorInfo;
                    }
                })
                .processor(classifierCompositeItemProcessProcessor())
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .build();
    }

    @Bean
    public ItemProcessor<? super ProcessorInfo, ? extends ProcessorInfo> classifierCompositeItemProcessProcessor() {
        ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();

        ClassifierCompositeItemProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> processorClassifier = new ClassifierCompositeItemProcessorClassifier<>();

        Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();
        processorMap.put(1, new ClassifierCompositeItemProcessor1());
        processorMap.put(2, new ClassifierCompositeItemProcessor2());
        processorMap.put(3, new ClassifierCompositeItemProcessor3());

        processorClassifier.setProcessorMap(processorMap);
        processor.setClassifier(processorClassifier);
        return processor;
    }
}
