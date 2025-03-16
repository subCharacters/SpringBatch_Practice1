package com.practice.springbatch_practice1.config.listener.jobstepexecution;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import java.time.Duration;

public class JobStepExecutionAnnotationListener {
    @BeforeJob
    public void beforeAnnotationJob(JobExecution jobExecution) {
        System.out.println("Job execution started. job name is " + jobExecution.getJobInstance().getJobName());
        System.out.println("Annotation");
    }

    @AfterJob
    public void afterAnnotationJob(JobExecution jobExecution) {
        System.out.println("execution time is: " + Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis() + "ms");
        System.out.println("Annotations");
    }
}
