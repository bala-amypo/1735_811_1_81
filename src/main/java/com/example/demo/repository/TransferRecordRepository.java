package com.example.demo.repository;

import com.example.demo.entity.TransferRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferRecordRepository extends JpaRepository<TransferRecord, Long> {

    List<TransferRecord> findByAssetId(Long assetId);
}
