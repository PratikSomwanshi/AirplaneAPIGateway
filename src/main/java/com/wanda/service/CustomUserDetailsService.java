package com.wanda.service;

import com.wanda.dto.CustomUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Lazy
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public CustomUserDetails loadUserByUsername(String email)  {
        try {

            return new CustomUserDetails(this.userService.getUser(email));

        }catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
