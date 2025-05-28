package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.RealWedding;
import com.wedstra.app.wedstra.backend.Services.RealWeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/real-weddings")
@CrossOrigin(origins = "*") // You can specify frontend origin here
public class RealWeddingController {

    @Autowired
    private RealWeddingService service;

    // ✅ Create
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadRealWedding(
            @RequestParam("title") String title,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        RealWedding created = service.createRealWedding(title, files);

        if(created != null){
            return ResponseEntity.ok(created);
        }else{
            return new ResponseEntity<>("Error adding Real Wedding", HttpStatus.BAD_REQUEST);
        }
    }


    // 📋 Get All
    @GetMapping("/all")
    public ResponseEntity<List<RealWedding>> getAllWeddings() {
        return ResponseEntity.ok(service.getAllRealWeddings());
    }

    // ❌ Delete by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWedding(@PathVariable String id) {
        boolean deleted = service.deleteRealWedding(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("RealWedding not found.");
        }
    }
}
