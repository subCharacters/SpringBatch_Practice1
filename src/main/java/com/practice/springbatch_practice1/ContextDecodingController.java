package com.practice.springbatch_practice1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class ContextDecodingController {

    private final JdbcTemplate jdbcTemplate;

    public ContextDecodingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/job-execution-context")
    public String getJobExecutionContext(@RequestParam Long jobExecutionId) {
        String encodedContext = jdbcTemplate.queryForObject(
                "SELECT short_context FROM batch_job_execution_context " +
                        "WHERE job_execution_id = ?"
                , new Object[]{jobExecutionId}
                , String.class
        );
        return decodedShortConText(encodedContext);
    }

    private String decodedShortConText(String encodedContext) {
        byte[] decodedContext = Base64.getDecoder().decode(encodedContext);
        return new String(decodedContext, StandardCharsets.UTF_8);
    }
}
