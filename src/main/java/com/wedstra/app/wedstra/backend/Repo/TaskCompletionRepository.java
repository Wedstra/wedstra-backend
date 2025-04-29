package com.wedstra.app.wedstra.backend.Repo;

import com.wedstra.app.wedstra.backend.Entity.TaskCompletions;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCompletionRepository extends MongoRepository<TaskCompletions, String> {
    Optional<TaskCompletions> findByUserIdAndTaskId(String userId, String taskId);
    List<TaskCompletions> findByUserId(String userId);
}
