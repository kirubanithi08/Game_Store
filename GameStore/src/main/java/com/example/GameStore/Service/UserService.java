package com.example.GameStore.Service;

import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
private  final UserRepository userRepository;

public UserService(UserRepository userRepository){
    this.userRepository=userRepository;
}


    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public void deleteGame(Long id){
    userRepository.deleteById(id);
    }
}
