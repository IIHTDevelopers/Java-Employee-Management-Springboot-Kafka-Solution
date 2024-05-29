package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployeeEvent {
    private String eventId;
    private EmployeeEventType eventType;
    private EmployeeDto employeeDto;
    private String eventDetails;

    public EmployeeEvent() {

    }
}

