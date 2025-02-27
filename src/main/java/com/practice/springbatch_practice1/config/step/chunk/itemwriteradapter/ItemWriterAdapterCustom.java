package com.practice.springbatch_practice1.config.step.chunk.itemwriteradapter;

// <T> 제네릭을 굳이 사용 안해도 됨
public class ItemWriterAdapterCustom {

    public void customWriter(Object item) {
        System.out.println(item);
    }
}
