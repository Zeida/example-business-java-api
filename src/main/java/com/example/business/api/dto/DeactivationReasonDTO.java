package com.example.business.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class DeactivationReasonDTO {
    private Long id;

    private String deactivationReason;

    @JsonBackReference
    private ItemDTO item;

    private UserDTO creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }
}
