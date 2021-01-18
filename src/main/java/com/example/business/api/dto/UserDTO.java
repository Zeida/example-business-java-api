package com.example.business.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Set;

public class UserDTO implements Serializable {
    private Long id;

    private String username;

    private String password;

    @JsonBackReference
    private Set<ItemDTO> items;

    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ItemDTO> getItems() {
        return items;
    }

    public void setItems(Set<ItemDTO> items) {
        this.items = items;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
