package com.example.GameStore.Service;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private  final UserRepository userRepository;

 public AuthService(UserRepository userRepository){
this.userRepository=userRepository;
}


    public User register(AuthRequest authRequest){

     if (userRepository.findByUsername(authRequest.getUsername()).isPresent()){
         throw new RuntimeException("Username already taken");
     }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());

       return userRepository.save(user);
    }
}
