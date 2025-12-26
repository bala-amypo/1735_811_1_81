package com.example.demo.service.impl;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.repository.DisposalRecordRepository;
import com.example.demo.service.DisposalRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DisposalRecordServiceImpl implements DisposalRecordService {

    private final DisposalRecordRepository repo;

    public DisposalRecordServiceImpl(DisposalRecordRepository repo) {
        this.repo = repo;
    }

    public DisposalRecord saveDisposal(DisposalRecord record) {
        return repo.save(record);
    }

    public List<DisposalRecord> getAllDisposals() {
        return repo.findAll();
    }
}
