package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.dto.UserRequestDTO;
import com.teetech.expensetrackerapi.dto.UserResponseDTO;
import com.teetech.expensetrackerapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    //UserRequestDTO -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    User toUser(UserRequestDTO requestDTO);

    //User -> UserResponseDTO
    UserResponseDTO toUserDTO(User user);

}
