package com.practice.springbatch_practice1.config.step.chunk;

import org.springframework.batch.item.*;

import java.util.List;

// ItemReader와 ItemStream이 같이 있는 ItemStreamReader를 implements
public class CustomItemStreamReader implements ItemStreamReader {

    private final List<String> items;
    private int index = -1;
    private boolean restart = false;

    public CustomItemStreamReader(final List<String> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        String item = null;

        // 몇 번째까지 읽었는지 인덱스를 설정
        if (this.index < this.items.size()) {
            item = this.items.get(index);
            index++;
        }

        // 특정 인덱스까지 읽었을때 일부러 에러를 발생시킴.
        // restart는
        if (this.index == 6 && !restart) {
            throw new RuntimeException("Restart is required");
        }

        return item;
    }

    // 시작할때 index의 값이 있는지 확인하고 없으면 새로 넣어줌.
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("index")) {
            index = executionContext.getInt("index");
            this.restart = true;
        } else {
            index = 0;
            executionContext.put("index", index);
        }
    }

    // 한사이클이 끝났을때 값을 갱신
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        ItemStreamReader.super.close();
        System.out.println("close");
    }

}
