package by.itsupportme.trainee.vacationmanagmentsystem.mappers;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.EmployeeDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, PositionMapper.class,
        ListVacationsMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "isFired", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "vacations", ignore = true)
    @Mapping(target = "boss", ignore = true)
    Employee findById(Long id);

    @Mapping(target = "departmentDto", source = "employee.department")
    @Mapping(target = "positionDto", source = "employee.position")
    @Mapping(target = "vacationDto", source = "employee.vacations")
    @Mapping(target = "bossId", source = "employee.boss.id")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "department", source = "employeeDto.departmentDto")
    @Mapping(target = "position", source = "employeeDto.positionDto")
    @Mapping(target = "vacations", source = "employeeDto.vacationDto")
    @Mapping(target = "boss", source = "employeeDto.bossId")
    Employee toEntity(EmployeeDto employeeDto);
}
