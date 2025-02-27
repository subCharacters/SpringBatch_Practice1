package com.practice.springbatch_practice1.config.repeatanderrorhandling.repeat;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RepeatJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public RepeatJobConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job repeatJob() {
        return new JobBuilder("repeatJob", jobRepository)
                .start(repeatStep())
                .build();
    }

    @Bean
    public Step repeatStep() {
        return new StepBuilder("repeatStep", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 3 ? null : "item " + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    RepeatTemplate template = new RepeatTemplate();
                    @Override
                    public String process(String item) throws Exception {
                        template.setCompletionPolicy(new SimpleCompletionPolicy()); // 기본 chunk size 5
                        template.setCompletionPolicy(new TimeoutTerminationPolicy(3000)); // 3초 설정. 3초 지나면 빠져나옴

                        // 두개 다 사용하고 싶을 경우. OR 조건으로 하나를 만족하면 빠져나옴.
                        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
                        CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
                                new SimpleCompletionPolicy(),
                                new TimeoutTerminationPolicy(3000)
                        };
                        compositeCompletionPolicy.setPolicies(completionPolicies);

                        template.setCompletionPolicy(compositeCompletionPolicy);

                        // 빈으로 설정 안하고 new로 할 시 인수가 계속 초기화 되면서 끝나지 않는다.
                        // 예외가 발생해도 지정 횟수까지는 무시하고 반복하는 폴리시이다.
                        template.setExceptionHandler(simpleLimitExceptionHandlerBean());

                        template.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                System.out.println("processing " + item);
                                return RepeatStatus.CONTINUABLE;
                            }
                        });

                        return item;
                    }
                })
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .build();
    }

    @Bean
    public ExceptionHandler simpleLimitExceptionHandlerBean() {
        return new SimpleLimitExceptionHandler(3);
    }
}
