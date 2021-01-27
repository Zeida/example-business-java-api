package com.example.business.api.service;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public UserDTO login(String username, String password) {
        UserDTO user = userRepository.findByUsername(username).map(this::convert2DTO).orElse(null);

        if(user != null) {
            if(BCrypt.checkpw(password, user.getPassword())) {
                user.setToken(getJWTTokenByUser(user));
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The user and/or password is incorrect");
    }

    public Iterable<UserDTO> findAllUsers() {
        Iterable<User> users = userRepository.findAll();
        return convertIterable2DTO(users);
    }

    public void saveUser(UserDTO dto) {
        if(userRepository.findByUsername(dto.getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Invalid username, '%s' already exists", dto.getUsername()));

        if(dto.getUsername().isEmpty() || dto.getUsername() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A username must be provided");

        if(dto.getPassword().isEmpty() || dto.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A password must be provided");

        User user = convert2Entity(dto);
        if(user != null) {
            Set<Item> allItems = processItems(user);
            if(allItems == null)
                allItems = new HashSet<>();

            user.setItems(allItems);
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            userRepository.save(user);
        }
    }

    @Transactional
    public void removeUser(UserDTO dto) {
        Optional<User> user = userRepository.findByUsername(dto.getUsername());
        if(!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user to remove does not exist.");
        }
        if(user.get().getItems() != null) {
            for(Item item : user.get().getItems()) {
                item.setCreator(null);
            }
        }
        user.get().setItems(null);
        userRepository.delete(user.get());
    }

    private String getJWTTokenByUser(UserDTO user) {
        String secretKey = "mySecretKey";
        String token = Jwts
                .builder()
                .setId("id")
                .setSubject(user.getUsername())
                .claim("authorities", "ROLE_" + user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }

    public UserDTO convert2DTO(User entity) {
        if(entity != null)
            return modelMapper.map(entity, UserDTO.class);
        return null;
    }

    public User convert2Entity(UserDTO dto) {
        if(dto != null)
            return modelMapper.map(dto, User.class);
        return null;
    }

    public Iterable<UserDTO> convertIterable2DTO(Iterable<User> iterableEntities) {
        if(iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(item -> modelMapper.map(item, UserDTO.class))
                    .collect(Collectors.toSet());
        return null;
    }

    public Iterable<User> convertIterable2Entity(Iterable<UserDTO> iterableDTOs) {
        if(iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(itemDTO -> modelMapper.map(itemDTO, User.class))
                    .collect(Collectors.toSet());
        return null;
    }

    private Set<Item> processItems(User user) {
        Set<Item> existingItems = new HashSet<>();
        if(user.getItems() != null)
            existingItems = user.getItems().stream()
                    .filter(item -> Objects.nonNull(item.getId()))
                    .collect(Collectors.toSet());

        Set<Item> allItems = new HashSet<>();
        if(user.getItems() != null)
            allItems = user.getItems().stream()
                .filter(item -> Objects.isNull(item.getId()))
                .collect(Collectors.toSet());

        for(Item item : existingItems) {
            Optional<Item> itemDB = itemRepository.findByCode(item.getCode());
            if(!itemDB.isPresent())
                return null;
            allItems.add(itemDB.get());
        }

        return allItems;
    }
}
