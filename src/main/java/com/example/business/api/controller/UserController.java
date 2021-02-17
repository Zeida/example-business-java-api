package com.example.business.api.controller;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", consumes = "application/json")
    public WebAsyncTask<UserDTO> login(@RequestBody UserDTO user) {
        return new WebAsyncTask<>(() -> userService.login(user.getUsername(), user.getPassword()));
    }

    @GetMapping(path = "/users")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<UserDTO>> allUsers() {
        return new WebAsyncTask<>(() -> userService.findAllUsers());
    }

    @PostMapping(path = "/users")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> addUser(@RequestBody UserDTO user) {
        return new WebAsyncTask<>(() -> userService.saveUser(user));
    }

    @DeleteMapping(path = "/users/{username}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> removeUser(@PathVariable String username) {
        return new WebAsyncTask<>(() -> userService.removeUser(username));
    }
}
