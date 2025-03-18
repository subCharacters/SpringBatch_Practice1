package com.practice.springbatch_practice1.config.listener.skiplistener;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

import java.util.LinkedList;
import java.util.List;

public class LinkedListItemReader<T> implements ItemReader<T> {

    private List<T> items;

    public LinkedListItemReader(List<T> list) {
        if (AopUtils.isAopProxy(list)) {
            this.items = list;
        } else {
            this.items = new LinkedList<>(list);
        }
    }

    @Override
    @Nullable
    public T read() throws CustomSkipException {
        if (!items.isEmpty()) {
            T remove = (T)items.remove(0);
            if ((Integer)remove == 3) {
                throw new CustomSkipException("read skipped : " + remove);
            }
            System.out.println("read : " + remove);
            return remove;
        }
        return null;
    }
}
