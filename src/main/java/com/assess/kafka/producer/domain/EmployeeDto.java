package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Integer id;
    private String username;
    private String password;
    private String address;
    private Long phoneNumber;
    private double salary;
    private Integer experience;

    public EmployeeDto() {
    }
}
