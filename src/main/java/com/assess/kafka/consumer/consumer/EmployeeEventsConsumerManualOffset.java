package com.assess.kafka.consumer.consumer;

import com.assess.kafka.consumer.entity.Employee;
import com.assess.kafka.consumer.service.KafkaEmployeeConsumerService;
import com.assess.kafka.consumer.service.EmployeeEventsService;
import com.assess.kafka.producer.domain.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeEventsConsumerManualOffset implements AcknowledgingMessageListener<String, EmployeeEvent> {
    @Autowired
    private KafkaEmployeeConsumerService employeeConsumerService;
    @Autowired
    private EmployeeEventsService employeeEventsService;

    @Override
    @KafkaListener(topics = "${spring.kafka.employee.topic.create-employee}", groupId = "${spring.kafka.employee.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, EmployeeEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("EmployeeEventsConsumerManualOffset Received message from Kafka: " + consumerRecord.value());
            if (consumerRecord.offset() % 2 == 0) {
                throw new RuntimeException("This is really odd.");
            }
            processMessage(consumerRecord.value());
            acknowledgment.acknowledge();
    }

    private void processMessage(EmployeeEvent employeeEvent) {
        Employee dbEmployee = employeeConsumerService.listenCreateEmployee(employeeEvent.getEmployeeDto());
        employeeEventsService.listenCreateEmployeeEvent(employeeEvent, dbEmployee);
    }

}