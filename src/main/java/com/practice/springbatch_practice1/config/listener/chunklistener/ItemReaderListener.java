package com.practice.springbatch_practice1.config.listener.chunklistener;

import org.springframework.batch.core.ItemReadListener;

public class ItemReaderListener implements ItemReadListener<String> {
    @Override
    public void beforeRead() {
        System.out.println(" >> before read");
    }

    @Override
    public void afterRead(String item) {
        System.out.println(" >> after read");
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println(" >> onReadError");
    }
}
