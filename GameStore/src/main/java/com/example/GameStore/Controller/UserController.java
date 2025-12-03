package com.example.GameStore.Controller;

import com.example.GameStore.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
private final UserService userService;

public UserController(UserService userService){
    this.userService=userService;
}

    @GetMapping
    ResponseEntity<?>getUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ){
        return ResponseEntity.ok(userService.getUsers(page, size));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?>deleteUser(@PathVariable Long id){
        userService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
