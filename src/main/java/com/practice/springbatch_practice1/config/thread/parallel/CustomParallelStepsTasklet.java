package com.practice.springbatch_practice1.config.thread.parallel;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CustomParallelStepsTasklet implements Tasklet {
    long sum = 0;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        for (int i = 0; i < 1000000000; i++) {
            sum++;
        }

        System.out.println("Sum: " + sum);

        return RepeatStatus.FINISHED;
    }
}
