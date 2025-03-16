package com.practice.springbatch_practice1.config.listener.jobstepexecution;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobStepExecutionListenerForStep implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("step is " + stepExecution.getStepName());
        stepExecution.getExecutionContext().put("custom", "aa");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus exitStatus = stepExecution.getExitStatus();
        System.out.println("exitStatus is " + exitStatus.getExitCode());
        BatchStatus batchStatus = stepExecution.getStatus();
        System.out.println("batchStatus is " + batchStatus);

        System.out.println(stepExecution.getExecutionContext().get("custom"));

        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
