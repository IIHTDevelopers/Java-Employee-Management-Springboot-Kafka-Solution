package com.assess.kafka.producer.controller;

import com.assess.kafka.producer.domain.EmployeeDto;
import com.assess.kafka.producer.domain.EmployeeEvent;
import com.assess.kafka.producer.producer.EmployeeEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeEventProducer employeeEventProducer;

    @PostMapping("/")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto requestedEmployeeDto) {
        try {
            EmployeeEvent employeeEvent = employeeEventProducer.sendCreateEmployeeEvent(requestedEmployeeDto, "Employee Enroll");
            return ResponseEntity.status(HttpStatus.CREATED).body(employeeEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending Employee event");
        }
    }
}
