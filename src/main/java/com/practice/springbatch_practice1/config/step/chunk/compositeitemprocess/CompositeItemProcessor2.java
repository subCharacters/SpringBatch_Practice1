package com.practice.springbatch_practice1.config.step.chunk.compositeitemprocess;

import org.springframework.batch.item.ItemProcessor;

public class CompositeItemProcessor2 implements ItemProcessor<String, String> {
    @Override
    public String process(String item) throws Exception {
        return item + " is processed";
    }
}
