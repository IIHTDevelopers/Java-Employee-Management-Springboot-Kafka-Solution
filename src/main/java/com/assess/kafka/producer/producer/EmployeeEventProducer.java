package com.assess.kafka.producer.producer;

import com.assess.kafka.producer.domain.EmployeeDto;
import com.assess.kafka.producer.domain.EmployeeEvent;
import com.assess.kafka.producer.domain.EmployeeEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class EmployeeEventProducer {

    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    @Value("${spring.kafka.employee.topic.create-employee}")
    private String topic;

    @Autowired
    public EmployeeEventProducer(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public EmployeeEvent sendCreateEmployeeEvent(EmployeeDto employeeDto, String eventDetails) {
        EmployeeEvent employeeEvent = EmployeeEvent.
                builder()
                .employeeDto(employeeDto)
                .eventType(EmployeeEventType.EMPLOYEE_ENROLL)
                .eventDetails(eventDetails + employeeDto.getUsername())
                .build();
        try {
            CompletableFuture<SendResult<String, EmployeeEvent>> sendResultCompletableFuture = kafkaTemplate.send(topic, employeeEvent.getEventType().toString(), employeeEvent);
           return sendResultCompletableFuture.get().getProducerRecord().value();
        } catch (Exception e) {
            log.debug("Error occurred while publishing message due to " + e.getMessage());
        }
        return employeeEvent;
    }
}
