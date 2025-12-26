package com.example.demo.repository;

import com.example.demo.entity.TransferRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRecordRepository extends JpaRepository<TransferRecord, Long> {
    // Matches test cases t43 and t81: findByAsset_Id
    List<TransferRecord> findByAsset_Id(Long assetId);
}