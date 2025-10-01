package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.Service;
import com.wedstra.app.wedstra.backend.Services.ServiceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")

public class ServiceController {

    @Autowired
    private ServiceServices serviceServices;

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable String id) {
        Service service = serviceServices.getServiceById(id);
        return ResponseEntity.ok(service);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable String id, @RequestBody Service serviceDetails) {
        try {
            // Call the service layer to perform the update logic
            Service updatedService = serviceServices.updateService(id, serviceDetails);
            // If successful, return the updated object with a 200 OK status
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            // This catches the "Service not found" exception from the service layer
            // and returns a more appropriate 404 Not Found status.
            return ResponseEntity.notFound().build();
        }
    }

//    @PutMapping("/{service_id}")
//    public ResponseEntity<?> handleUpdateService(
//            @PathVariable String service_id,
//            @RequestBody String service_name
//    ){
//        System.out.println("Service Id = "+service_id);
//        System.out.println("Service Name = "+service_name);
//        return new ResponseEntity<String>("Update API working", HttpStatus.OK);
//    }



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
    public ResponseEntity<List<Service>> handleGetServicesForVendorByCategoryByCity(@PathVariable String category, @PathVariable String location,@PathVariable String vendor_id) {
        return new ResponseEntity<>(serviceServices.getServicesByVendorByLocationByCategory(category, location,vendor_id), HttpStatus.OK);
    }

    @DeleteMapping("{service_id}/delete")
    public ResponseEntity<?> handleServiceDelete(@PathVariable String service_id, @RequestParam String vendorId) {
        if (serviceServices.deleteService(service_id, vendorId)) {
            return new ResponseEntity<>("Service deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("service not found", HttpStatus.NOT_FOUND);
        }
    }
}
