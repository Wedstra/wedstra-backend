package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.Service;
import com.wedstra.app.wedstra.backend.Services.ServiceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/service")

public class ServiceController {

    @Autowired
    private ServiceServices serviceServices;

    @GetMapping("/getAll")
    public ResponseEntity<List<Service>> handleGetAllServices() {
        return new ResponseEntity<List<Service>>(serviceServices.getAllServices(), HttpStatus.OK);
    }

    //get Services for perticular vendor
    @GetMapping("/{vendor_id}/all")
    public ResponseEntity<List<Service>> handleGetServicesByVendor(@PathVariable String vendor_id) {
        return new ResponseEntity<>(serviceServices.getServicesByVendor(vendor_id), HttpStatus.OK);
    }

    @PostMapping("/{vendor_id}/create-service")
    public ResponseEntity<?> handleCreateService(
            @RequestParam("service_name") String service_name,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("min_price") String min_price,
            @RequestParam("max_price") String max_price,
            @RequestParam("location") String location,
            @RequestParam("files") List<MultipartFile> files,
            @PathVariable String vendor_id) throws IOException {
        try {
            Service createdService = serviceServices.createService(service_name, description, category, min_price, max_price, location, files, vendor_id);
            return new ResponseEntity<>(createdService, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Service>> handleGetServicesByCategory(@PathVariable String category) {
        return new ResponseEntity<>(serviceServices.getServicesBycategory(category), HttpStatus.OK);
    }

    @GetMapping("/by-location/{location}")
    public ResponseEntity<List<Service>> handleGetServicesByLocation(@PathVariable String location) {
        return new ResponseEntity<>(serviceServices.getServicesByLocation(location), HttpStatus.OK);
    }

    @GetMapping("/by-vendor/{vendor_id}/by-location/{location}")
    public ResponseEntity<List<Service>> handleGetServicesByLocation(@PathVariable String location, @PathVariable String vendor_id) {
        return new ResponseEntity<>(serviceServices.getServicesByVendorByLocation(location, vendor_id), HttpStatus.OK);
    }

    @GetMapping("/by-vendor/{vendor_id}/by-category/{category}")
    public ResponseEntity<List<Service>> handleGetServicesByCategory(@PathVariable String category, @PathVariable String vendor_id) {
        return new ResponseEntity<>(serviceServices.getServicesByVendorByCategory(category, vendor_id), HttpStatus.OK);
    }

    @GetMapping("/by-vendor/{vendor_id}/by-location/{location}/by-category/{category}")
    public ResponseEntity<List<Service>> handleGetServicesByCategory(@PathVariable String category, @PathVariable String location,@PathVariable String vendor_id) {
        return new ResponseEntity<>(serviceServices.getServicesByVendorByLocationByCategory(category, location,vendor_id), HttpStatus.OK);
    }





    @DeleteMapping("{service_id}/delete")
    public ResponseEntity<?> handleServiceDelete(@PathVariable String service_id) {
        if (serviceServices.deleteService(service_id)) {
            return new ResponseEntity<>("Service deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("service not found", HttpStatus.NOT_FOUND);
        }
    }
}
