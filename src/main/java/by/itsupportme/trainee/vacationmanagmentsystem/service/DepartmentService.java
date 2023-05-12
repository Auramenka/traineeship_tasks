package by.itsupportme.trainee.vacationmanagmentsystem.service;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {
    DepartmentDto saveDepartment(DepartmentDto departmentDto);
    List<DepartmentDto> getAllDepartments();
    void deleteDepartment(Long id);
    DepartmentDto updateDepartment(DepartmentDto departmentDto);
    DepartmentDto findById(Long id);
}
