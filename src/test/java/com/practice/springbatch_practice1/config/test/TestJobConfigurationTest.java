package com.practice.springbatch_practice1.config.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.Date;

@SpringBootTest(classes = {TestJobConfiguration.class, TestBatchConfig.class})
@SpringBatchTest
@ExtendWith(SpringExtension.class)
class TestJobConfigurationTest {

    // 빨간 줄(컴파일에러)는 인텔리제이에서 path를 못 찾는가봄
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJob_test() throws Exception {

        // given
        // this.jdbcTemplate.update("");
        JobParameters jobParameter = new JobParametersBuilder()
                .addString("name", "testUser")
                .addDate("date", new Date())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameter);

        // then
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assertions.assertEquals(ExitStatus.COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
    }

    @Test
    public void testJob_test_Step() throws Exception {

        // given
        // this.jdbcTemplate.update("");
        JobParameters jobParameter = new JobParametersBuilder()
                .addString("name", "testUser")
                .addDate("date", new Date())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("testStep");

        // then
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        stepExecutions.forEach(stepExecution -> {
            // 실제로는 100*100으로 10번 읽어서 처리되나
            // null 확인 후 처리가 종료되기 때문에 11번째 리딩이 발생한다.
            // 이때 카운트도 한번 증가되기 때문에 테스트시 주의가 필요.
            // 경우에 따라 다르니 잘 생각해서 해야 할듯.
            Assertions.assertEquals(11, stepExecution.getCommitCount());
            Assertions.assertEquals(1000, stepExecution.getReadCount());
            Assertions.assertEquals(1000, stepExecution.getWriteCount());
        });
    }
}