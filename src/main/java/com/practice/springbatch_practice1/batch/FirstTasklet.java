package com.practice.springbatch_practice1.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirstTasklet implements Tasklet {
    private String msg;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info(msg);
        System.out.println(msg);
        return RepeatStatus.FINISHED;
    }

    public void setMsg(String paramMsg) {
        this.msg = paramMsg;
    }
}
