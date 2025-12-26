package com.example.demo.repository;

import com.example.demo.entity.DisposalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisposalRecordRepository extends JpaRepository<DisposalRecord, Long> {
    // Standard JPA methods are sufficient for this repository
}