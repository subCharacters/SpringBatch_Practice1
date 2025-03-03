package com.practice.springbatch_practice1.config.repeatanderrorhandling.repeat;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/*
 * item1,2,3,4가 있다고 가정하고 item2에서 에러가 발생했을때
 * itemreader의 경우 item2번을 제외한 나머지를 process에서 전달.
 * process의 경우 reader로 다시 돌아가서 캐싱한 reader 데이터를 받는데 item2를 빼고 재처리.
 * writer의 경우도 process와 마찬가지로 itme2를 빼고 다시 처리한다.
 * 결론은 item2를 빼로 처리한다는 소리이다.
 */
@Configuration
public class SkipConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public SkipConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job skipJob() {
        return new JobBuilder("skipJob", jobRepository)
                .start(skipStep())
                .incrementer(new RunIdIncrementer())
                .build();

    }

    @Bean
    public Step skipStep() {
        return new StepBuilder("skipStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>() {
                    int count = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        count++;
                        if (count == 3) {
                            throw new NonTransientResourceException("This is a itemreader exception");
                        }
                        System.out.println("item reader" + count);
                        return count > 20 ? null : String.valueOf(count);
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        if (item.equals("6") || item.equals("7")) {
                            System.out.println("item process if = " + item);
                            throw new NonTransientResourceException("This is a itemprocess exception");
                        }

                        System.out.println("item process else = " + item);
                        return String.valueOf(Integer.valueOf(item) * -1);
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.getItems().forEach(item -> {
                            if (item.equals("-12")) {
                                System.out.println("item writer if = " + item);
                                throw new NonTransientResourceException("This is a itemwriter exception");
                            }
                            System.out.println("item writer else = " + item);
                        });
                    }
                })
                .faultTolerant()
                // .skip(NonTransientResourceException.class)
                // .skipLimit(4)
                // 위를 직접 구현.
                .skipPolicy(skipCustomPolicy())
                // 스킵하지 않는다. 기본적으로 설정이 필요없으나 세세한 컨트롤이 가능하다.
                // 무슨 말이냐면 skip에서 모든 Exception을 대상으로 했다고 했지만 특정 예외는 noskip으로 하고 싶을때
                // exceptio을 상속한 하위 예외를 넣어두면 skip하지 않는다.
                .noSkip(IllegalArgumentException.class)
                .build();
    }

    @Bean
    public SkipPolicy skipCustomPolicy() {
        Map<Class<? extends Throwable>, Boolean> skipPolicyMap = new HashMap<>();
        skipPolicyMap.put(NonTransientResourceException.class, true);

        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(4, skipPolicyMap);
        return limitCheckingItemSkipPolicy;
    }
}
