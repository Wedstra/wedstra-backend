package com.wedstra.app.wedstra.backend.Controller;
import com.wedstra.app.wedstra.backend.Entity.StateCity;
import com.wedstra.app.wedstra.backend.Services.StateCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Set;

@RestController
@RequestMapping("/location")
public class StateCityController {

    @Autowired
    private StateCityService service;

    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(service.getAllStates());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCitiesByState(@RequestParam String state) {
        List<String> cities = service.getCitiesByState(state);
        if (cities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cities);
    }
}


