package com.practice.springbatch_practice1.config.step.chunk.jpaitemwriter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Customer2 {

    @Id
    private int id;
    private String firstName;
    private String lastName;
}
