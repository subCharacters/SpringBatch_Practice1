package com.practice.springbatch_practice1.config.step.chunk.itemreaderadapter;

public class ItemReaderAdapterService<T> {
    private int cnt = 0;

    public T customRead() {
        if (cnt > 100) {
            return null;
        }
        return (T) ("item : " + cnt++);
    }
}
