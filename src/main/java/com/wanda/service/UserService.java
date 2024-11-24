package com.wanda.service;

import com.wanda.entity.Roles;
import com.wanda.entity.Users;
import com.wanda.repository.RolesRepository;
import com.wanda.repository.UserRepository;
import com.wanda.utils.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private RolesRepository rolesRepository;
    private AuthenticationManager authManager;
    private JWTService jwtService;

    public UserService(UserRepository userRepository,
                       RolesRepository rolesRepository,
                       AuthenticationManager authManager,
                       JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public Users getUser(String email){
        Optional<Users> existingUser = this.userRepository.findByEmail(email);

        if(existingUser.isPresent()){
            return existingUser.get();
        }

        throw new UsernameNotFoundException("User not found");
    }

    public Users saveUser(Users user){

        var existingUser = this.userRepository.findByEmail(user.getEmail());

        if(existingUser.isPresent()){
            throw new CustomException("User already exists", HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        Set<Roles> roles = new HashSet<>();

        user.getRoles().forEach(role -> {
            var existingRole = this.rolesRepository.findByAuthority(role.getAuthority());

            if(existingRole.isPresent()){
                roles.add(existingRole.get());
            }else{
                roles.add(role);
            }
        });

        user.setRoles(roles);


        return this.userRepository.save(user);
    }

    public String verify(Users user){

        Authentication authenticate = this.authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        if(authenticate.isAuthenticated()){
//            TODO: jwt generate
            return this.jwtService.generateToken(user);
        }

        throw new CustomException("Invalid email or password", HttpStatus.BAD_REQUEST);
    }
}
