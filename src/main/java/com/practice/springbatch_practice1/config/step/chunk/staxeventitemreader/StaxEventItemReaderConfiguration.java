package com.practice.springbatch_practice1.config.step.chunk.staxeventitemreader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StaxEventItemReaderConfiguration {

    @Bean
    public Job staxEventItemReaderJob(JobRepository jobRepository
            , Step staxEventItemReaderStep) {
        return new JobBuilder("staxEventItemReaderJob", jobRepository)
                .start(staxEventItemReaderStep)
                .build();
    }

    @Bean
    public Step staxEventItemReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("staxEventItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(2, transactionManager)
                .reader(staxEventItemReaderReader())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        chunk.getItems().forEach(System.out::println);
                    }
                })
                .build();
    }

    private ItemReader<? extends Customer> staxEventItemReaderReader() {
        return new StaxEventItemReaderBuilder<Customer>()
                .name("staxEventItemReaderReader")
                .resource(new ClassPathResource("/customer.xml"))
                .addFragmentRootElements("customer") // 어느 단위로 조각낼건지 설정
                .unmarshaller(staxEventItemReaderUnmarshaller())
                .build();
    }

    private Unmarshaller staxEventItemReaderUnmarshaller() {
        Map<String, Class<?>> unmarshallers = new HashMap<>();
        unmarshallers.put("customer", Customer.class);
        unmarshallers.put("id", Long.class);
        unmarshallers.put("name", String.class);
        unmarshallers.put("age", Integer.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();

        XStream xStream = xStreamMarshaller.getXStream();
        xStreamMarshaller.setAliases(unmarshallers);

        // xstream보안으로 인해 추가적으로 모든 클래스 차단 후
        xStream.addPermission(NoTypePermission.NONE);
        // xstream보안으로 인해 추가적으로 허용할 클래스만 지정 필요
        xStream.allowTypes(new Class[]{Customer.class});

        /* 패키지 단위 허용 (선택 사항)
        xStream.allowTypesByWildcard(new String[]{
                "com.practice.springbatch_practice1.config.step.chunk.staxeventitemreader.*"
        });
         */

        // xstream보안으로 인해 추가적으로 매핑할 클래스의 어노테이션 추가 및 아래 코드 추가
        xStream.processAnnotations(Customer.class);
        return xStreamMarshaller;
    }
}
