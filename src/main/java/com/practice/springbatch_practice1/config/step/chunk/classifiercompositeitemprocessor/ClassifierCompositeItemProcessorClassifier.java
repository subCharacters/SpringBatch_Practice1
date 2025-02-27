package com.practice.springbatch_practice1.config.step.chunk.classifiercompositeitemprocessor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

public class ClassifierCompositeItemProcessorClassifier<C, T> implements Classifier<C, T> {
    private Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();

    @Override
    public T classify(C o) {
        return (T)processorMap.get(((ProcessorInfo) o).getId());
    }

    public void setProcessorMap(Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap) {
        this.processorMap = processorMap;
    }
}
