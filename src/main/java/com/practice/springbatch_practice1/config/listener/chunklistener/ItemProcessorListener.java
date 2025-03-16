package com.practice.springbatch_practice1.config.listener.chunklistener;

import org.springframework.batch.core.ItemProcessListener;

public class ItemProcessorListener implements ItemProcessListener<Integer, String> {
    @Override
    public void beforeProcess(Integer item) {
        System.out.println(" >> before process");
    }

    @Override
    public void afterProcess(Integer item, String result) {
        System.out.println(" >> afterProcess");
    }

    @Override
    public void onProcessError(Integer item, Exception e) {
        System.out.println(" >> onProcessError");
    }
}
