package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 커스텀으로 구현한 클래스
 */
public class CustomJobParametersIncrementer implements JobParametersIncrementer {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public JobParameters getNext(JobParameters parameters) {

        String id = dateFormat.format(new Date());

        return new JobParametersBuilder().addString("run.id", id).toJobParameters();
    }
}
