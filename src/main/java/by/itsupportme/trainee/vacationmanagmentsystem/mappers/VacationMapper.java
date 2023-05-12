package by.itsupportme.trainee.vacationmanagmentsystem.mappers;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacationMapper {
    @Mapping(target = "employeeId", source = "employee.id")
    VacationDto toDto(Vacation vacation);
    @Mapping(target = "employee", ignore = true)
    Vacation toEntity(VacationDto vacationDto);
}