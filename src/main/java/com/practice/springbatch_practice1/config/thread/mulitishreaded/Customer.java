package com.practice.springbatch_practice1.config.thread.mulitishreaded;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String birthdate;
}
