package com.practice.springbatch_practice1.config.step.chunk.classifiercompositeitemprocessor;

import org.springframework.batch.item.ItemProcessor;

public class ClassifierCompositeItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        System.out.println("ClassifierCompositeItemProcessor3");
        return item;
    }
}
