package by.itsupportme.trainee.vacationmanagmentsystem.service;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto saveEmployee(EmployeeDto employeeDto);
    List<EmployeeDto> getAllEmployees();
    void deleteEmployee(Long id);
    EmployeeDto updateEmployee(EmployeeDto employeeDto);
    EmployeeDto findById(Long id);
    List<EmployeeDto> findByIds(List<Long> ids);
}
