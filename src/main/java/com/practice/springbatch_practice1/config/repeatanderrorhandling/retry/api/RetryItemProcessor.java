package com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.api;

import com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.RetryableException;
import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessor implements ItemProcessor<String, String> {

    private int cnt = 0;
    @Override
    public String process(String item) throws Exception {
        cnt++;

        if (item.equals("2") || item.equals("3")) {
            // retry 시 처음으로 다시 돌아가서 청크를 시작. 아이템1부터 시작함.
            cnt++;
            throw new RetryableException("failed cnt :" + cnt);
        }
        return item;
    }
}
