package com.example.GameStore.Service;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.UserRepository;

public class AuthService {

    private  final UserRepository userRepository;

 public AuthService(UserRepository userRepository){
this.userRepository=userRepository;
}


    public User register(AuthRequest authRequest){


        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());

       return userRepository.save(user);
    }
}
