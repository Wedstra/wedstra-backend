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

import java.security.Principal;
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

    @DeleteMapping("/custom/{taskId}")
    public ResponseEntity<?> deleteCustomTask(@PathVariable String taskId) {
        taskServices.deleteCustomTask(taskId);
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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


    @PostMapping("/mark-complete")
    public ResponseEntity<String> updateTaskCompletion(@RequestBody TaskCompletions request) {
        System.out.println("User Marking status:"+request.getUserId());
        try {
            taskServices.markTaskCompletion(request.getTaskId(), request.isCompleted(), request.getUserId());
            return ResponseEntity.ok("Task completion updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}/completion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uncheckTask(@PathVariable String taskId,
                            @RequestParam(required = false) String userId,
                            Principal principal) {

        // If you trust Spring Security, get the logged-in user from Principal
        String effectiveUserId = (userId != null) ? userId : principal.getName();

        taskServices.deleteCompletion(taskId, effectiveUserId);
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
