package com.teetech.expensetrackerapi.mapper;

import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import com.teetech.expensetrackerapi.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {MapperHelper.class},unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ExpenseMapper {
    //ExpenseRequestDTO -> Expense
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Expense toExpense(ExpenseRequestDTO requestDTO);

    //Expense -> ExpenseResponseDTO
    @Mapping(source = "category", target = "categoryId", qualifiedByName = "extractCategoryId")
    @Mapping(source = "category", target = "categoryName", qualifiedByName = "extractCategoryName")
    ExpenseResponseDTO toExpenseDTO(Expense expense);

    //ExpenseUpdateDTO -> Expense
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Expense toExpenseUpdate(ExpenseUpdateDTO updateDTO, @MappingTarget Expense expense);
}
