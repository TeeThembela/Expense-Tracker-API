package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetResponseDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import com.teetech.expensetrackerapi.entity.Budget;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MapperHelper.class},unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BudgetMapper {
    //BudgetRequestDTO -> Budget
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toBudget(BudgetRequestDTO requestDTO);

    //Budget ->  BudgetResponseDTO
    @Mapping(source = "category", target = "categoryId", qualifiedByName = "extractCategoryId")
    @Mapping(source = "category", target = "categoryName", qualifiedByName = "extractCategoryName")
    BudgetResponseDTO toBudgetDTO(Budget budget);

    //BudgetUpdateDTO -> Budget
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toBudgetUpdate(BudgetUpdateDTO updateDTO,
                          @MappingTarget Budget budget);
}
