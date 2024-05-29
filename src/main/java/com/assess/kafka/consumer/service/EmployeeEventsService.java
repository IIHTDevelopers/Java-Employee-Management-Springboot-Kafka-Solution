package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Employee;
import com.assess.kafka.consumer.entity.EmployeeEvent;
import com.assess.kafka.consumer.entity.EmployeeEventType;
import com.assess.kafka.consumer.jpa.EmployeeEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.assess.kafka.consumer.entity.EmployeeEventType.*;

@Service
public class EmployeeEventsService {

    private final EmployeeEventRepository employeeEventRepository;

    @Autowired
    public EmployeeEventsService(EmployeeEventRepository employeeEventRepository) {
        this.employeeEventRepository = employeeEventRepository;
    }


    public void listenCreateEmployeeEvent(com.assess.kafka.producer.domain.EmployeeEvent employeeEvent, Employee dbEmployee) {
        EmployeeEvent event = EmployeeEvent.builder()
                .eventType(mapEventType(employeeEvent.getEventType()))
                .eventDetails(employeeEvent.getEventDetails())
                .employeeId(Long.valueOf(dbEmployee.getId()))
                .build();
        employeeEventRepository.save(event);
    }

    private EmployeeEventType mapEventType(com.assess.kafka.producer.domain.EmployeeEventType eventType) {
        return switch (eventType) {
            case EMPLOYEE_ENROLL -> EMPLOYEE_ENROLL;
            case EMPLOYEE_RESIGN -> EMPLOYEE_RESIGN;
        };
    }
}
