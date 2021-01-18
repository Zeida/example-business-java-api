package com.example.business.api.controller;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", consumes = "application/json")
    public UserDTO login(@RequestBody UserDTO user) {
        return userService.login(user.getUsername(), user.getPassword());
    }
}
