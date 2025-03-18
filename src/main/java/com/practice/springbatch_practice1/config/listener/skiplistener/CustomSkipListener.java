package com.practice.springbatch_practice1.config.listener.skiplistener;

import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<Integer, String> {
    @Override
    public void onSkipInRead(Throwable t) {
        System.out.println("CustomSkipListener onSkipInRead");
    }

    @Override
    public void onSkipInProcess(Integer item, Throwable t) {
        System.out.println("CustomSkipListener onSkipInProcess");
    }

    @Override
    public void onSkipInWrite(String item, Throwable t) {
        System.out.println("CustomSkipListener onSkipInWrite");
    }
}
