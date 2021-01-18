package com.example.business.api.service;

import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.User;
import com.example.business.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    public UserDTO login(String username, String password) {
        UserDTO user = userRepository.findByUsername(username).map(this::convert2DTO).orElse(null);

        if(user != null) {
            if(BCrypt.checkpw(password, user.getPassword())) {
                user.setToken(getJWTTokenByUsername(username));
                return user;
            }
        }
        return null;
    }

    private String getJWTTokenByUsername(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("id")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
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
}
