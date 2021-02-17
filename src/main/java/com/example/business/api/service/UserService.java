package com.example.business.api.service;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.User;

public interface UserService extends BaseService<User, UserDTO> {
    UserDTO login(String username, String password);
    Iterable<UserDTO> findAllUsers();
    Void saveUser(UserDTO dto);
    Void removeUser(String username);
}
