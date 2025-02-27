package com.practice.springbatch_practice1.config.flowjob;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomExitStatus implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if (ExitStatus.COMPLETED.getExitCode().equals(exitCode)) {
            return new ExitStatus("PASS");
        } else {
            return null;
        }
    }
}
