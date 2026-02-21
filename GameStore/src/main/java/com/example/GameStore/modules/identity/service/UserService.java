package com.example.GameStore.modules.identity.service;

import com.example.GameStore.modules.identity.dto.UserResponse;
import com.example.GameStore.modules.identity.repository.UserRepository;
import com.example.GameStore.modules.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserResponse> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                ));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
