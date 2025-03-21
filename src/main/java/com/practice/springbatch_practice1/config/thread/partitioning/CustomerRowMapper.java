package com.practice.springbatch_practice1.config.thread.partitioning;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(rs.getInt("id")
        , rs.getString("firstName")
        , rs.getString("lastName")
        , rs.getString("birthdate")
        );
    }
}
