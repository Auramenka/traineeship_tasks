package by.itsupportme.trainee.vacationmanagmentsystem.mappers;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ListVacationsMapper {
    @Mapping(target = "employeeId", source = "employee.id")
    VacationDto toDto(Vacation vacation);
    @Mapping(target = "employee", ignore = true)
    Vacation toEntity(VacationDto vacationDto);
    List<VacationDto> toDtoList(List<Vacation> vacations);
    List<Vacation> toEntityList(List<VacationDto> vacationDtoList);
}
