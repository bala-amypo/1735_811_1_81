package com.example.demo.service;

import com.example.demo.entity.DisposalRecord;
import java.util.List;

public interface DisposalRecordService {

    DisposalRecord createDisposal(Long assetId, DisposalRecord disposal);

    DisposalRecord getDisposal(Long id);

    List<DisposalRecord> getAllDisposals();
}
