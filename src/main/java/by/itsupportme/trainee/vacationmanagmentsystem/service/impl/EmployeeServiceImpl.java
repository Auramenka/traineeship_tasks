package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.EmployeeDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.EmployeeMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        if (employeeDto == null) {
            throw new NotExistsException("EmployeeDto is empty");
        }
        return employeeMapper.toDto(employeeRepository.save(employeeMapper.toEntity(employeeDto)));
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAllEmployees().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto findById(Long id) {
        return employeeMapper.toDto(getEmployeeFromDB(id));
    }

    public List<EmployeeDto> findByIds(List<Long> ids) {
        return employeeRepository.findByEmployeeIds(ids).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        Employee employeeFromDB = getEmployeeFromDB(id);
        employeeRepository.deleteById(employeeFromDB.getId());
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getDepartmentDto() == null) {
            throw new NotExistsException("DepartmentDto can't be null");
        }

        if (employeeDto.getPositionDto() == null) {
            throw new NotExistsException("PositionDto can't be null");
        }

        Employee employeeFromDB = getEmployeeFromDB(employeeDto.getId());

        Department departmentFromDB = departmentRepository.findById(employeeDto.getDepartmentDto().getId())
                .orElseThrow(() -> new NotExistsException("Department doesn't exist"));

        Position positionFromDB = positionRepository.findById(employeeDto.getPositionDto().getId())
                .orElseThrow(() -> new NotExistsException("Position doesn't exist"));

        if (employeeFromDB.getId().equals(employeeDto.getBossId())) {
            throw new RuntimeException("Employee can't be boss with the same id");
        }

        Employee bossFromDB = employeeRepository.findById(employeeDto.getBossId())
                .orElseThrow(() -> new NotExistsException("Employee boss doesn't exist"));

        employeeFromDB.setDepartment(departmentFromDB);
        employeeFromDB.setPosition(positionFromDB);
        employeeFromDB.setBoss(bossFromDB);
        employeeMapper.updateEmployee(employeeDto, employeeFromDB);
        return employeeMapper.toDto(employeeRepository.save(employeeFromDB));
    }

    private Employee getEmployeeFromDB(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NotExistsException("Employee doesn't exist"));
    }
}
