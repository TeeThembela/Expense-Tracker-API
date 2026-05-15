package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.entity.Category;
import com.teetech.expensetrackerapi.entity.User;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MapperHelper {

    // ID extraction
    @Named("extractCategoryId")
    public UUID extractCategoryId(Category category){
        return (category != null && category.getId() != null) ? category.getId() : null;
    }
    @Named("extractUserId")
    public UUID extractUserId(User user){
        return (user != null && user.getId() != null) ? user.getId() : null;
    }

    //Category name extraction
    @Named("extractCategoryName")
    public String extractCategoryName(Category category){
        return (category != null && category.getName() != null) ? category.getName() : "";
    }

}
