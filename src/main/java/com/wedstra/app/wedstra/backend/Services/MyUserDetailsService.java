package com.wedstra.app.wedstra.backend.Services;

import com.wedstra.app.wedstra.backend.Entity.User;
import com.wedstra.app.wedstra.backend.Entity.UserPrincipal;
import com.wedstra.app.wedstra.backend.Entity.Vendor;
import com.wedstra.app.wedstra.backend.Entity.VendorPrincipal;
import com.wedstra.app.wedstra.backend.Repo.UserRepo;
import com.wedstra.app.wedstra.backend.Repo.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    VendorRepository vendorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if(user == null){
            System.out.println("User Not Found");
            Vendor vendor = vendorRepository.findByUsername(username);
            if(vendor != null){
                System.out.println("Vendor Found");
                return new VendorPrincipal(vendor);
            }
            else{
                System.out.println("Vendor Not Found");
                throw new UsernameNotFoundException("User not found");
            }
        }
        return new UserPrincipal(user);
    }
}
