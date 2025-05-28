package com.eventhub.AuthMicroService.dto.mappers;

import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapping {
    UserDataDTO toDTO(User user);
}
