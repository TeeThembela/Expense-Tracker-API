package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryResponseDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelper.class},unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {
    //CategoryRequestDTO -> Category
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "predefinedCategory", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    Category toCategory(CategoryRequestDTO requestDTO);

    //Category -> CategoryResponseDTO
    CategoryResponseDTO toCategoryDTO(Category category);

    //CategoryUpdateDTO ->
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "predefinedCategory", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    Category toCategoryUpdate(CategoryUpdateDTO updateDTO, @MappingTarget Category category);
}
