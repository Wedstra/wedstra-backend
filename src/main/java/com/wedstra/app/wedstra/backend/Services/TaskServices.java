package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.TaskCompletions;
import com.wedstra.app.wedstra.backend.Entity.TaskWithCompletionDTO;
import com.wedstra.app.wedstra.backend.Entity.Tasks;
import com.wedstra.app.wedstra.backend.Repo.TaskCompletionRepository;
import com.wedstra.app.wedstra.backend.Repo.TasksRepository;
import com.wedstra.app.wedstra.backend.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServices {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    public TaskCompletionRepository taskCompletionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepo userRepository;

    private final LocalDateTime localDateTime = LocalDateTime.now();

    public Tasks creteNewTask(Tasks task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        task.setCreateAt(formattedDateTime);
        return tasksRepository.save(task);

    }

    public List<Tasks> getAllPredefinedTasks() {
        Query query = new Query(Criteria.where("type").is("predefined"));
        return mongoTemplate.find(query, Tasks.class);
    }


    public void markTaskCompletion(String taskId, boolean completed, String userMarkingComplete) throws Exception {
        System.out.println(userMarkingComplete);
        Tasks taskFromDb = tasksRepository.findById(taskId)
                .orElseThrow(() -> new Exception("Task not found"));

        String userId = userMarkingComplete; // or get from session/token if needed

        // Try to find existing task completion
        Optional<TaskCompletions> existingCompletion =
                taskCompletionRepository.findByUserIdAndTaskId(userId, taskId);

        if (existingCompletion.isPresent()) {
            TaskCompletions taskCompletion = existingCompletion.get();
            taskCompletion.setCompleted(completed);
            taskCompletion.setCompletedAt(completed ? getCurrentFormattedDateTime() : null);
            taskCompletionRepository.save(taskCompletion);
        } else {
            // New entry
            TaskCompletions taskCompletion = new TaskCompletions(
                    userId,
                    taskId,
                    completed,
                    completed ? getCurrentFormattedDateTime() : null
            );
            taskCompletionRepository.save(taskCompletion);
        }
    }

    public void deleteCompletion(String taskId, String userId) {

        boolean exists = taskCompletionRepository
                .existsByUserIdAndTaskId(userId, taskId);
        if (!exists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Completion record not found for taskId=" + taskId +
                            ", userId=" + userId);
        }

        taskCompletionRepository.deleteByUserIdAndTaskId(userId, taskId);
    }

    private String getCurrentFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public List<Tasks> getCustomTasksByUser(String userId) {
        Query query = new Query(Criteria.where("createdBy").is(userId));
        return mongoTemplate.find(query, Tasks.class);
    }

    public List<TaskWithCompletionDTO> getAllTasksWithStatus(String userId) {
        if (!userRepository.existsById(userId)) {
            return null; // or throw new UserNotFoundException(userId);
        }

        List<Tasks> predefinedTasks = getAllPredefinedTasks();
        List<Tasks> customTasks = getCustomTasksByUser(userId);

        List<Tasks> allTasks = new ArrayList<>();
        allTasks.addAll(predefinedTasks);
        allTasks.addAll(customTasks);

        Set<String> completedTaskIds = taskCompletionRepository
                .findByUserId(userId)
                .stream()
                .map(TaskCompletions::getTaskId)
                .collect(Collectors.toSet());

        return allTasks.stream()
                .map(task -> new TaskWithCompletionDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getType(),
                        task.getPhase(),
                        task.getTask(),
                        completedTaskIds.contains(task.getId())
                ))
                .collect(Collectors.toList());
    }
}

