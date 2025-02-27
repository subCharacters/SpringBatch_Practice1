package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

/**
 * 커스텀으로 구현한 클래스
 */
public class CustomJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        if (jobParameters.getString("name") == null) {
            throw new JobParametersInvalidException("name is required");
        }
    }
}
