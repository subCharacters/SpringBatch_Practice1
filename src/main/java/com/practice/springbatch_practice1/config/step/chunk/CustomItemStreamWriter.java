package com.practice.springbatch_practice1.config.step.chunk;

import org.springframework.batch.item.*;

public class CustomItemStreamWriter implements ItemStreamWriter<String> {
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(item -> {
            System.out.println("chunk = " + chunk);
        });
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        //ItemStreamWriter.super.open(executionContext);
        System.out.println("writer open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //ItemStreamWriter.super.update(executionContext);
        System.out.println("writer update");
    }

    @Override
    public void close() throws ItemStreamException {
        //ItemStreamWriter.super.close();
        System.out.println("writer close");
    }
}
