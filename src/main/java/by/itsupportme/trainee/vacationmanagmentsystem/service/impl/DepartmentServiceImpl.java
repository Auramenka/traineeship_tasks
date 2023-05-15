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
        return departmentMapper.toDto(getDepartmentFromDB(id));
    }

    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteDepartment(Long id) {
        Department departmentFromDB = getDepartmentFromDB(id);
        departmentRepository.deleteById(departmentFromDB.getId());
    }

    public DepartmentDto updateDepartment(DepartmentDto departmentDto) {
        checkDepartmentDto(departmentDto);
        Department departmentFromDB = getDepartmentFromDB(departmentDto.getId());
        departmentFromDB.setName(departmentDto.getName());
        return departmentMapper.toDto(departmentRepository.save(departmentFromDB));
    }

    private void checkDepartmentDto(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            throw new NotExistsException("DepartmentDto is empty");
        }
    }

    private Department getDepartmentFromDB(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new NotExistsException("Department doesn't exist"));
    }
}