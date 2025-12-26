package com.example.demo.service;

import com.example.demo.entity.TransferRecord;
import com.example.demo.exception.ValidationException;

import java.util.List;

public interface TransferRecordService {

    TransferRecord createTransfer(Long assetId, TransferRecord transfer) throws ValidationException;

    List<TransferRecord> getTransfersForAsset(Long assetId);
}
