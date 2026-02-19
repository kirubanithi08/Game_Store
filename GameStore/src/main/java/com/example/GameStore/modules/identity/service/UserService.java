package com.example.GameStore.modules.identity.service;

import com.example.GameStore.modules.identity.entity.User;
import com.example.GameStore.modules.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
