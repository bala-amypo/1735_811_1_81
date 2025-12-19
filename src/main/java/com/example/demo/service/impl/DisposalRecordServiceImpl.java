package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.entity.DisposalRecord;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.DisposalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DisposalRecordServiceImpl implements DisposalRecordService {

    private final DisposalRecordRepository repo;
    private final AssetRepository assetRepo;

    public DisposalRecordServiceImpl(DisposalRecordRepository repo,
                                     AssetRepository assetRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
    }

    @Override
    public DisposalRecord createDisposal(Long assetId, DisposalRecord disposal) {

        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        if (disposal.getDisposalDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Disposal date cannot be in the future");
        }

        User approver = disposal.getApprovedBy();
        if (approver == null || !"ADMIN".equalsIgnoreCase(approver.getRole())) {
            throw new ValidationException("Approved by must be ADMIN");
        }

        asset.setStatus("DISPOSED");
        assetRepo.save(asset);

        disposal.setAsset(asset);
        return repo.save(disposal);
    }

    @Override
    public DisposalRecord getDisposal(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found"));
    }

    @Override
    public List<DisposalRecord> getAllDisposals() {
        return repo.findAll();
    }
}
