package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.TaskCompletions;
import com.wedstra.app.wedstra.backend.Entity.TaskWithCompletionDTO;
import com.wedstra.app.wedstra.backend.Entity.Tasks;
import com.wedstra.app.wedstra.backend.Repo.TaskCompletionRepository;
import com.wedstra.app.wedstra.backend.Services.TaskServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TaskServices taskServices;

    @Autowired
    private TaskCompletionRepository taskCompletionRepository;


    //for creating custom tasks for premium users only
    @PostMapping("/create")
    public ResponseEntity<Tasks> createTask(@RequestBody Tasks task) {
        Tasks createdTask = taskServices.creteNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    //for getting all predefined tasks
    @GetMapping("/get-predefined-all")
    public ResponseEntity<?> getPredefinedTasks(){
        List<Tasks> tasks = taskServices.getAllPredefinedTasks();
        return ResponseEntity.ok(tasks);
    }

    //get all custom with specific userId
    @GetMapping("/get-custom/{userId}")
    public ResponseEntity<?> getCustomTasks(@PathVariable String userId){
        List<Tasks> tasks = taskServices.getCustomTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }


    //for getting all predefined tasks
    @GetMapping("/get-custom-all")
    public ResponseEntity<?> getCustomTasks(){
        return ResponseEntity.ok("Custom tasks successfully");
    }

//    @PostMapping
//    public ResponseEntity<?> markTaskComplete(@PathVariable String id) throws Exception {
//        taskServices.markTaskCompletion(id);
//        return ResponseEntity.ok("Task marked as complete successfully");
//    }

    @PostMapping("/mark-complete")
    public ResponseEntity<String> updateTaskCompletion(@RequestBody TaskCompletions request) {
        try {
            taskServices.markTaskCompletion(request.getTaskId(), request.isCompleted());
            return ResponseEntity.ok("Task completion updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/all-tasks-with-status/{userId}")
    public ResponseEntity<?> getAllTasksWithCompletionStatus(@PathVariable String userId) {
        List<TaskWithCompletionDTO> allTasks = taskServices.getAllTasksWithStatus(userId);
        if (allTasks == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
        return ResponseEntity.ok(allTasks);
    }






//    @PostMapping("{id}/mark-uncomplete")
//    public ResponseEntity<?> markTaskUnComplete(@PathVariable String id) throws Exception {
//        taskServices.markUnCompletedTask(id);
//        return ResponseEntity.ok("Task marked as complete successfully");
//    }
}
