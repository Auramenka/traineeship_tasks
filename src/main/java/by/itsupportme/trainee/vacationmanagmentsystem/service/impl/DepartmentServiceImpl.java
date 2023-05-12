package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.DepartmentMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentDto saveDepartment(DepartmentDto dto) {
        checkDepartmentDto(dto);
        return departmentMapper.toDto(
                departmentRepository.save(departmentMapper.toEntity(dto)));
    }

    public DepartmentDto findById(Long id) {
        Department departmentFromDb = departmentRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(DEPARTMENT_DOES_NOT_EXIST));
        return departmentMapper.toDto(departmentFromDb);
    }

    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(DEPARTMENT_DOES_NOT_EXIST));
        departmentRepository.deleteById(department.getId());
    }

    public DepartmentDto updateDepartment(DepartmentDto departmentDto) {
        checkDepartmentDto(departmentDto);
        Department departmentFromDB = departmentRepository.findById(departmentDto.getId())
                .orElseThrow(() -> new NotExistsException(DEPARTMENT_DOES_NOT_EXIST));
        departmentFromDB.setName(departmentDto.getName());
        return departmentMapper.toDto(departmentRepository.save(departmentFromDB));
    }

    private void checkDepartmentDto(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            throw new NotExistsException(DEPARTMENT_DTO_IS_EMPTY);
        }
    }
}