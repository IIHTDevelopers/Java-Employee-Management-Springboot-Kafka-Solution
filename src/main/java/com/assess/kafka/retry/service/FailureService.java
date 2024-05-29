package com.assess.kafka.retry.service;

import com.assess.kafka.producer.domain.EmployeeEvent;
import com.assess.kafka.retry.entity.FailureRecord;
import com.assess.kafka.retry.entity.FailureStatus;
import com.assess.kafka.retry.jpa.FailureRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FailureService {

    private final FailureRecordRepository failureRecordRepository;
    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Value(value = "${spring.kafka.employee.topic.create-employee}")
    private String topic;

    @Autowired
    public FailureService(FailureRecordRepository failureRecordRepository, KafkaTemplate<String, EmployeeEvent> kafkaTemplate,
                          ObjectMapper objectMapper) {
        this.failureRecordRepository = failureRecordRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void retryFailedOperations() {
        Iterable<FailureRecord> failedRecords = failureRecordRepository.findAllByStatus(FailureStatus.FAILED);
        for (FailureRecord failureRecord : failedRecords) {
            try {
                processFailedOperation(failureRecord);
                failureRecordRepository.delete(failureRecord);
            } catch (Exception e) {
                System.err.println("Error processing failed operation: " + e.getMessage());
            }
        }
    }

    private void processFailedOperation(FailureRecord failureRecord) {
        boolean success = retryOperation(failureRecord);
        if (success) {
            failureRecord.setStatus(FailureStatus.RETRIED);
            failureRecordRepository.save(failureRecord);
        } else {
            failureRecord.setStatus(FailureStatus.PERMANENTLY_FAILED);
            failureRecordRepository.save(failureRecord);
        }
    }

    private boolean retryOperation(FailureRecord failureRecord) {
        try {
            EmployeeEvent employeeEvent = objectMapper.readValue(failureRecord.getMessage(), EmployeeEvent.class);
            kafkaTemplate.send(topic, employeeEvent.getEventType().toString(), employeeEvent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
