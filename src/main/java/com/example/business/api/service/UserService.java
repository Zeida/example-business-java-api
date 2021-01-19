package com.example.business.api.service;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.User;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface UserService extends BaseService<User, UserDTO> {
    UserDTO login(String username, String password);
    Iterable<UserDTO> findAllUsers();
    void saveUser(UserDTO dto);
    void removeUser(UserDTO dto);
}
