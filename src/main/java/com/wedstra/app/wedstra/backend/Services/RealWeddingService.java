package com.wedstra.app.wedstra.backend.Services;


import com.wedstra.app.wedstra.backend.Entity.RealWedding;
import com.wedstra.app.wedstra.backend.Repo.RealWeddingRepository;
import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class RealWeddingService {

    @Autowired
    private RealWeddingRepository repository;

    @Autowired
    private FileStore fileStore;

    public List<RealWedding> getAllRealWeddings() {
        return repository.findAll();
    }

    public boolean deleteRealWedding(String id) {
        Optional<RealWedding> optional = repository.findById(id);
        if (optional.isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public RealWedding createRealWedding(String title, List<MultipartFile> files) throws IOException {
        RealWedding newRealWedding = new RealWedding();
        newRealWedding.setTitle(title);
        repository.save(newRealWedding); // Save initially to generate ID

        String realWeddingId = newRealWedding.getId();
        Map<String, String> metadata = new HashMap<>();
        List<String> photoUrls = new ArrayList<>();

        for (MultipartFile realWeddingPhoto : files) {
            String photoUrl = fileStore.saveRealWeddings(
                    realWeddingPhoto.getOriginalFilename(),
                    Optional.of(metadata),
                    realWeddingPhoto.getInputStream(),
                    generateKey(realWeddingPhoto, realWeddingId)
            );
            photoUrls.add(photoUrl); // ✅ Correct: directly add the returned URL
        }

        newRealWedding.setFileUrls(photoUrls);
        repository.save(newRealWedding); // Save again with file URLs

        return newRealWedding;
    }


    private String generateKey(MultipartFile file, String realWeddingId) {
        String key = realWeddingId +"/realweddings" + "/" + file.getOriginalFilename();
        return key;
    }
}

