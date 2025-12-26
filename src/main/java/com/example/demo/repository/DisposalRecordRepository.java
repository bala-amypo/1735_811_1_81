package com.example.demo.repository;

import com.example.demo.entity.DisposalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisposalRecordRepository extends JpaRepository<DisposalRecord, Long> {

    List<DisposalRecord> findByAssetId(Long assetId);
}
