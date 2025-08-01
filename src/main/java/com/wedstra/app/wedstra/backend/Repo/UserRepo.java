package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);

    boolean existsById(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String userId);
}
