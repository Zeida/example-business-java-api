package com.example.business.api.unittest;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.User;
import com.example.business.api.repository.UserRepository;
import com.example.business.api.service.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loginWithValidUser() {
        String username = "user";
        String password = "password";

        User user = new User(1L, username, password, null);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(username);
        user.setPassword(password);

        Mockito.doReturn(userDTO).when(userService).login(username, password);

        UserDTO actualUser = userService.login(username, password);

        Assert.assertEquals(username, actualUser.getUsername());
        Mockito.verify(userService, Mockito.times(1)).login(username, password);
    }

    @Test
    public void findAllUsers() {
        Set<User> users = new HashSet<>();
        Set<UserDTO> userDTOS = new HashSet<>();

        User user = new User(1L, "user", "pass", null);
        users.add(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTOS.add(userDTO);

        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.doReturn(userDTOS).when(userService).convertIterable2DTO(users);

        Iterable<UserDTO> actualUsers = userService.findAllUsers();
        UserDTO actualUser = actualUsers.iterator().next();

        Assert.assertEquals(new Long(1L), actualUser.getId());
        Mockito.verify(userService,
                Mockito.times(1)).findAllUsers();
    }

    @Test
    public void saveNewUser() {
        UserDTO userToAdd = new UserDTO();
        userToAdd.setUsername("user");
        userToAdd.setPassword("pass");

        User user = new User();
        user.setUsername(userToAdd.getUsername());
        user.setPassword(userToAdd.getPassword());

        Mockito.when(userRepository.findByUsername(userToAdd.getUsername())).thenReturn(Optional.empty());
        Mockito.doReturn(user).when(userService).convert2Entity(userToAdd);

        userService.saveUser(userToAdd);

        Mockito.verify(userService, Mockito.times(1)).saveUser(userToAdd);
    }

    @Test
    public void removeUser() {
        UserDTO userToRemove = new UserDTO();
        userToRemove.setUsername("user");

        User user = new User();
        user.setUsername(userToRemove.getUsername());

        Mockito.when(userRepository.findByUsername(userToRemove.getUsername())).thenReturn(Optional.of(user));

        userService.removeUser(userToRemove);

        Mockito.verify(userService, Mockito.times(1)).removeUser(userToRemove);
    }
}
