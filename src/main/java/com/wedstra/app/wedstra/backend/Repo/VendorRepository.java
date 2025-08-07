package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.Vendor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends MongoRepository<Vendor, ObjectId> {
    void deleteById(String id);

    Vendor findByUsername(String username);

    Optional<Vendor> findByEmail(String email);

    List<Vendor> findByIdIn(List<String> ids);
}
