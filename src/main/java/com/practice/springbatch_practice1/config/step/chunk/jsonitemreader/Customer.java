package com.practice.springbatch_practice1.config.step.chunk.jsonitemreader;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;


@Data
@XStreamAlias("customer")
public class Customer {
    private long id;
    private String name;
    private int age;
}
