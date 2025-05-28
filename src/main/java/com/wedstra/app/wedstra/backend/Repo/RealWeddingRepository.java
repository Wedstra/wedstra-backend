package com.wedstra.app.wedstra.backend.Repo;


import com.wedstra.app.wedstra.backend.Entity.RealWedding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealWeddingRepository extends MongoRepository<RealWedding, String> {
}

