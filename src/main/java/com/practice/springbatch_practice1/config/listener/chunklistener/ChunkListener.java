package com.practice.springbatch_practice1.config.listener.chunklistener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;

public class ChunkListener {

    @BeforeChunk
    public void beforeChunk() {
        System.out.println(" >> before chunk");
    }

    @AfterChunk
    public void afterChunk() {
        System.out.println(" >> after chunk");
    }

    @AfterChunkError
    public void afterChunkError() {
        System.out.println(" >> after chunk error");
    }
}
