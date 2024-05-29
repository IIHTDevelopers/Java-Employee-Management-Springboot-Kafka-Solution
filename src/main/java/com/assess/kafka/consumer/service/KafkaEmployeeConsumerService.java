package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Employee;
import com.assess.kafka.consumer.jpa.EmployeeRepository;
import com.assess.kafka.producer.domain.EmployeeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEmployeeConsumerService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee listenCreateEmployee(EmployeeDto employeeDto) {
        Employee dbEmployee = Employee.builder()
                .address("america")
                .experience(5)
                .salary(100000d)
                .phoneNumber(111112456456l)
                .build();
        return employeeRepository.save(dbEmployee);
    }

}
