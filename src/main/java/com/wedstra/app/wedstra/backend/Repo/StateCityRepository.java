package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.StateCity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface StateCityRepository extends MongoRepository<StateCity, String> {
}

