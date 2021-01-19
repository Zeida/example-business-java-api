package com.example.business.api.controller;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", consumes = "application/json")
    public UserDTO login(@RequestBody UserDTO user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @GetMapping(path = "/users")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Iterable<UserDTO> allUsers() {
        return userService.findAllUsers();
    }

    @PostMapping(path = "/users")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addUser(@RequestBody UserDTO user) throws ChangeSetPersister.NotFoundException {
        userService.saveUser(user);
    }

    @DeleteMapping(path = "/users")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeUser(@RequestBody UserDTO user) throws ChangeSetPersister.NotFoundException {
        userService.removeUser(user);
    }
}
