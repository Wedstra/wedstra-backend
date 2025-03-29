package com.wedstra.app.wedstra.backend.Controller;

import com.wedstra.app.wedstra.backend.Entity.LoginRequest;
import com.wedstra.app.wedstra.backend.Entity.User;
import com.wedstra.app.wedstra.backend.Services.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;


    @PostMapping("/register")
    public ResponseEntity<String> handleUserRegister(@RequestBody User user){
        String message = userServices.createNewUser(user);
        if(message.contains("successfully")){
            return new ResponseEntity<>(message,HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> handleUserLogin(@RequestBody LoginRequest loginRequest, HttpSession session){
        try {
            String token = userServices.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            session.setAttribute("user", loginRequest.getUsername());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password",HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/get-all-user")
    public ResponseEntity<List<User>> handleGetALlUser(){
        return new ResponseEntity<>(userServices.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> handleGetUserById(@PathVariable String id) {
        User user = userServices.getUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-user/{username}")
    public ResponseEntity<User> hendleGetUserByUsername(@PathVariable String username){
        return new ResponseEntity<>(userServices.getUserByUsername(username), HttpStatus.OK);
    }


}
