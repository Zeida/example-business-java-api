package com.example.business.api.unittest;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.User;
import com.example.business.api.model.UserRoleEnum;
import com.example.business.api.repository.UserRepository;
import com.example.business.api.service.UserServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
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

    private static UserDTO testUserDTO;
    private static User testUser;

    private final static String USERNAME = "user";
    private final static String PASSWORD = "password";
    private final static Long ID = 1L;

    @BeforeClass
    public static void setupTest() {
        testUserDTO = new UserDTO();
        testUserDTO.setId(ID);
        testUserDTO.setUsername(USERNAME);
        testUserDTO.setPassword(PASSWORD);

        testUser = new User();
        testUser.setId(ID);
        testUser.setUsername(USERNAME);
        testUser.setPassword(PASSWORD);
    }

    @Test
    public void loginWithValidUser() {
        Mockito.doReturn(testUserDTO).when(userService).login(USERNAME, PASSWORD);

        UserDTO actualUser = userService.login(USERNAME, PASSWORD);

        Assert.assertEquals(testUserDTO.getUsername(), actualUser.getUsername());
        Mockito.verify(userService, Mockito.times(1)).login(USERNAME, PASSWORD);
    }

    @Test
    public void findAllUsers() {
        Set<User> users = new HashSet<>();
        Set<UserDTO> userDTOS = new HashSet<>();

        users.add(testUser);
        userDTOS.add(testUserDTO);

        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.doReturn(userDTOS).when(userService).convertIterable2DTO(users);

        Iterable<UserDTO> actualUsers = userService.findAllUsers();
        UserDTO actualUser = actualUsers.iterator().next();

        Assert.assertEquals(ID, actualUser.getId());
        Mockito.verify(userService,
                Mockito.times(1)).findAllUsers();
    }

    @Test
    public void saveNewUser() {
        Mockito.when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.doReturn(testUser).when(userService).convert2Entity(testUserDTO);

        userService.saveUser(testUserDTO);

        Mockito.verify(userService, Mockito.times(1)).saveUser(testUserDTO);
    }

    @Test
    public void removeUser() {
        Mockito.when(userRepository.findByUsername(testUserDTO.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.countByRole(UserRoleEnum.ADMIN)).thenReturn(99);

        userService.removeUser(testUserDTO.getUsername());

        Mockito.verify(userService, Mockito.times(1)).removeUser(testUserDTO.getUsername());
    }
}
