package com.assess.kafka.consumer.jpa;

import com.assess.kafka.consumer.entity.EmployeeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeEventRepository extends JpaRepository<EmployeeEvent, Long> {
    // Define custom query methods if needed
}