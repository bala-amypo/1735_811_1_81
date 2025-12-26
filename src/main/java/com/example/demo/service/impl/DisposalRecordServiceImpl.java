// // File: src/main/java/com/example/demo/service/impl/DisposalRecordServiceImpl.java
// package com.example.demo.service.impl;

// import com.example.demo.entity.Asset;
// import com.example.demo.entity.DisposalRecord;
// import com.example.demo.exception.ResourceNotFoundException;
// import com.example.demo.repository.AssetRepository;
// import com.example.demo.repository.DisposalRecordRepository;
// import com.example.demo.repository.UserRepository;
// import com.example.demo.service.DisposalRecordService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// @Service
// public class DisposalRecordServiceImpl implements DisposalRecordService {
    
//     @Autowired
//     private DisposalRecordRepository disposalRecordRepository;
    
//     @Autowired
//     private AssetRepository assetRepository;
    
//     @Autowired
//     private UserRepository userRepository;
    
//     @Override
//     public DisposalRecord createDisposal(Long assetId, DisposalRecord disposal) {
//         Asset asset = assetRepository.findById(assetId)
//             .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));
        
//         disposal.setAsset(asset);
//         DisposalRecord saved = disposalRecordRepository.save(disposal);
        
//         // Update asset status to DISPOSED
//         asset.setStatus("DISPOSED");
//         assetRepository.save(asset);
        
//         return saved;
//     }
    
//     @Override
//     public DisposalRecord getDisposal(Long id) {
//         return disposalRecordRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found with id: " + id));
//     }
// }
package com.example.demo.service.impl;

import com.example.demo.entity.Asset;
import com.example.demo.entity.DisposalRecord;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.DisposalRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DisposalRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DisposalRecordServiceImpl implements DisposalRecordService {

    private final DisposalRecordRepository disposalRecordRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public DisposalRecordServiceImpl(DisposalRecordRepository disposalRecordRepository,
                                     AssetRepository assetRepository,
                                     UserRepository userRepository) {
        this.disposalRecordRepository = disposalRecordRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DisposalRecord createDisposal(Long assetId, DisposalRecord disposal) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        User approver = userRepository.findById(disposal.getApprovedBy().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"ADMIN".equals(approver.getRole())) {
            throw new ValidationException("Approver must be ADMIN");
        }
        if (disposal.getDisposalDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Disposal date cannot be in the future");
        }

        asset.setStatus("DISPOSED");
        assetRepository.save(asset);

        disposal.setAsset(asset);
        disposal.setApprovedBy(approver);
        return disposalRecordRepository.save(disposal);
    }

    @Override
    public DisposalRecord getDisposal(Long id) {
        return disposalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found"));
    }

    @Override
    public List<DisposalRecord> getAllDisposals() {
        return disposalRecordRepository.findAll();
    }
}
