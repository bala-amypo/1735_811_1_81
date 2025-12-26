package com.example.demo.service;

import com.example.demo.entity.DisposalRecord;
import com.example.demo.exception.ValidationException;

public interface DisposalRecordService {

    DisposalRecord createDisposal(Long assetId, DisposalRecord disposal) throws ValidationException;

    DisposalRecord getDisposal(Long id);
}
