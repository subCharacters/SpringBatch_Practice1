package com.practice.springbatch_practice1.config.listener.chunklistener;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

public class ItemWriterListener implements ItemWriteListener<String> {
    @Override
    public void beforeWrite(Chunk<? extends String> items) {
        System.out.println(" >> Before Write");
    }

    @Override
    public void afterWrite(Chunk<? extends String> items) {
        System.out.println(" >> After Write");
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends String> items) {
        System.out.println(" >> OnWriteError Write");
    }
}
