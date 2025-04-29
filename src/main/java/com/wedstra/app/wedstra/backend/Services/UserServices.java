package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.User;
import com.wedstra.app.wedstra.backend.Repo.UserRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createNewUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setPlanType("FREE");
        User user1 = userRepo.save(user);
        if(user1 != null){
            return "user registration successfully.";
        }
        else{
            return "user registration failed.";
        }
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }


    public String authenticate(String username, String passwordHash){
        User user = userRepo.findByUsername(username);
        if(user != null){
            try {
                Authentication authentication = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), passwordHash)
                );
                if(authentication.isAuthenticated()){
                    return jwtServices.generateToken(username, user.getId());
                }
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }
        throw new BadCredentialsException("Invalid username or password");
    }


    public User getUserById(String id) {
        return userRepo.findById(new ObjectId(id)).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
