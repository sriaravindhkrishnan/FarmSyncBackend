package com.farmsync.FarmSyncBackend.service;

import com.farmsync.FarmSyncBackend.model.FarmDetails;
import com.farmsync.FarmSyncBackend.repository.FarmRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FarmService {

    private final FarmRepository farmRepository;

    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    public boolean create(FarmDetails farmDetails) {
        return farmRepository.save(farmDetails);
    }

    public List<FarmDetails> getFarms(String userID) {
        return farmRepository.getActiveFarms(userID);
    }
}
