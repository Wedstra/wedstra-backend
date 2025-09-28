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

    @GetMapping("/vendors/states")
    public ResponseEntity<List<String>> getDistinctStates() {
        List<String> states = service.getDistinctStatesFromVendors();
        return ResponseEntity.ok(states);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCitiesByState(@RequestParam String state) {
        List<String> cities = service.getCitiesByState(state);
        if (cities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/vendors/states/{state}/cities")
    public ResponseEntity<List<String>> getDistinctCitiesInState(@PathVariable String state) {
        List<String> cities = service.getDistinctCitiesFromVendors(state);

        // Return the list of cities in the response body with a 200 OK status
        return ResponseEntity.ok(cities);
    }
}


