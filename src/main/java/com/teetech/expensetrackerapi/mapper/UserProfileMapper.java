package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import com.teetech.expensetrackerapi.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserProfileMapper {
    //UserProfileRequestDTO -> UserProfile
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toUserProfile(UserProfileRequestDTO requestDTO);

    //UserProfile -> UserProfileResponseDTO
    UserProfileResponseDTO toUserProfileDTO(UserProfile profile);

    //UserProfileUpdateDTO -> UserProfile
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toUserProfileUpdate(UserProfileUpdateDTO updateDTO,
                                       @MappingTarget UserProfile profile);
}
