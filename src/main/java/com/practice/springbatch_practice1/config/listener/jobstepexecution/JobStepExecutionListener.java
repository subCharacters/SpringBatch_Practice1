package com.practice.springbatch_practice1.config.listener.jobstepexecution;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;

public class JobStepExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job execution started. job name is " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("execution time is: " + Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis() + "ms");
    }
}
