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
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

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
            throw new NotExistsException(EMPLOYEE_DTO_IS_EMPTY);
        }
        return employeeMapper.toDto(employeeRepository.save(employeeMapper.toEntity(employeeDto)));
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAllEmployees().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(EMPLOYEE_DOES_NOT_EXIST));
        return employeeMapper.toDto(employee);
    }

    public List<EmployeeDto> findByIds(List<Long> ids) {
        return employeeRepository.findByEmployeeIds(ids).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(EMPLOYEE_DOES_NOT_EXIST));
        employeeRepository.deleteById(employee.getId());
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getDepartmentDto() == null) {
            throw new NotExistsException(DEPARTMENT_DTO_CAN_NOT_BE_NULL);
        }

        if (employeeDto.getPositionDto() == null) {
            throw new NotExistsException(POSITION_DTO_CAN_NOT_BE_NULL);
        }

        Employee employeeFromDB = employeeRepository.findById(employeeDto.getId())
                .orElseThrow(() -> new NotExistsException(EMPLOYEE_DOES_NOT_EXIST));

        Department departmentFromDB = departmentRepository.findById(employeeDto.getDepartmentDto().getId())
                .orElseThrow(() -> new NotExistsException(DEPARTMENT_DOES_NOT_EXIST));

        Position positionFromDB = positionRepository.findById(employeeDto.getPositionDto().getId())
                .orElseThrow(() -> new NotExistsException(POSITION_DOES_NOT_EXIST));

        if (employeeFromDB.getId().equals(employeeDto.getBossId())) {
            throw new RuntimeException(EMPLOYEE_CAN_NOT_BE_BOSS_WITH_THE_SAME_ID);
        }

        Employee bossFromDB = employeeRepository.findById(employeeDto.getBossId())
                .orElseThrow(() -> new NotExistsException(EMPLOYEE_BOSS_DOES_NOT_EXIST));

        employeeFromDB.setFirstName(employeeDto.getFirstName());
        employeeFromDB.setLastName(employeeDto.getLastName());
        employeeFromDB.setDateOfBirth(employeeDto.getDateOfBirth());
        employeeFromDB.setGender(employeeDto.getGender());
        employeeFromDB.setDepartment(departmentFromDB);
        employeeFromDB.setPosition(positionFromDB);
        employeeFromDB.setIsFired(employeeDto.getIsFired());
        employeeFromDB.setBoss(bossFromDB);
        return employeeMapper.toDto(employeeRepository.save(employeeFromDB));
    }
}
