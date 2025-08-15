package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.Blog;
import com.wedstra.app.wedstra.backend.Entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
}
