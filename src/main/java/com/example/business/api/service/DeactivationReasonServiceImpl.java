package com.example.business.api.service;

import com.example.business.api.dto.DeactivationReasonDTO;
import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.DeactivationReason;
import com.example.business.api.model.Item;
import com.example.business.api.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeactivationReasonServiceImpl implements DeactivationReasonService{
    @Autowired
    private ModelMapper modelMapper;

    public void mergeDTO2Entity(DeactivationReasonDTO dto, DeactivationReason entity, String mappingName) {

    }

    public DeactivationReasonDTO convert2DTO(DeactivationReason entity) {
        if(entity != null)
            return modelMapper.map(entity, DeactivationReasonDTO.class);
        return null;
    }

    public DeactivationReason convert2Entity(DeactivationReasonDTO dto) {
        if(dto != null)
            return modelMapper.map(dto, DeactivationReason.class);
        return null;
    }

    public Iterable<DeactivationReasonDTO> convertIterable2DTO(Iterable<DeactivationReason> iterableEntities) {
        if(iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(deactivationReason -> modelMapper.map(deactivationReason, DeactivationReasonDTO.class))
                    .collect(Collectors.toList());
        return null;
    }

    public Iterable<DeactivationReason> convertIterable2Entity(Iterable<DeactivationReasonDTO> iterableDTOs) {
        if(iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(deactivationReasonDTO -> modelMapper.map(deactivationReasonDTO, DeactivationReason.class))
                    .collect(Collectors.toList());
        return null;
    }
}
