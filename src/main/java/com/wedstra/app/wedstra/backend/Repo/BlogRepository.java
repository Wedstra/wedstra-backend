package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogRepository extends MongoRepository<Blog, String> {

}
