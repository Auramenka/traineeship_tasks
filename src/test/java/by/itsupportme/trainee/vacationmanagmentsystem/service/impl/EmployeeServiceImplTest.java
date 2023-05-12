package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.EmployeeDto;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.EmployeeMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    PositionRepository positionRepository;

    @Mock
    EmployeeMapper employeeMapper;

    @InjectMocks
    EmployeeServiceImpl testSubject;

    @Test
    void shouldSaveCurrentEmployee() {
        //given
        EmployeeDto employeeDto = new EmployeeDto();

        Employee employee = new Employee();
        when(employeeMapper.toEntity(employeeDto)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        when(employeeRepository.save(employee)).thenReturn(employee);
        //when
        EmployeeDto result = testSubject.saveEmployee(employeeDto);
        //then
        assertNotNull(result);
        verify(employeeMapper, times(1)).toDto(employee);
        verify(employeeMapper, times(1)).toEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void shouldNotSaveEmployee_thenException() {
        //given
        EmployeeDto employeeDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveEmployee(employeeDto));
        //then
        assertEquals(EMPLOYEE_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldGetCurrentEmployeeById() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);

        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        //when
        EmployeeDto result = testSubject.findById(id);
        //then
        assertNotNull(result);
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void shouldNotGetCurrentEmployeeById_thenException() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.findById(id));
        //then
        assertEquals(EMPLOYEE_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldGetCurrentEmployeesByIds() {
        //given
        Long id1 = 100L;
        Long id2 = 101L;

        List<Long> ids = new ArrayList<>();

        ids.add(id1);
        ids.add(id2);

        EmployeeDto employeeDto = new EmployeeDto();

        List<Employee> employees = new ArrayList<>();

        Employee firstEmployee = new Employee();
        firstEmployee.setId(id1);

        Employee secondEmployee = new Employee();
        secondEmployee.setId(id2);

        employees.add(firstEmployee);
        employees.add(secondEmployee);

        when(employeeRepository.findByEmployeeIds(ids)).thenReturn(employees);

        for (Employee employee : employees) {
            when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        }
        //when
        List<EmployeeDto> result = testSubject.findByIds(ids);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findByEmployeeIds(ids);
        for (Employee employee : employees) {
            verify(employeeMapper, times(1)).toDto(employee);
        }
    }

    @Test
    void shouldGetEmptyListWhenEmployeesByIdsNotExists() {
        List<Long> list = new ArrayList<>();
        //given
        when(employeeRepository.findByEmployeeIds(list)).thenReturn(Collections.emptyList());
        //when
        List<EmployeeDto> result = testSubject.findByIds(list);
        //then
        assertEquals(0, result.size());
        verify(employeeRepository, times(1)).findByEmployeeIds(list);
    }

    @Test
    void shouldGetAllCurrentEmployees() {
        //given
        EmployeeDto employeeDto = new EmployeeDto();

        Employee employee = new Employee();
        when(employeeRepository.findAllEmployees()).thenReturn(Collections.singletonList(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        //when
        List<EmployeeDto> result = testSubject.getAllEmployees();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAllEmployees();
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void shouldGetEmptyListWhenEmployeesNotExists() {
        //given
        when(employeeRepository.findAllEmployees()).thenReturn(Collections.emptyList());
        //when
        List<EmployeeDto> result = testSubject.getAllEmployees();
        //then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(employeeRepository, times(1)).findAllEmployees();
    }

    @Test
    void shouldDeleteExistedEmployee() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        //when
        testSubject.deleteEmployee(id);
        //then
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldNotDeleteEmployee_thenException() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.deleteEmployee(id));
        //then
        assertEquals(EMPLOYEE_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldReturnEmployeeById() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);

        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        //when
        EmployeeDto result = testSubject.findById(id);
        //then
        assertNotNull(result);
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void shouldNotReturnEmployeeById_thenException() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.findById(id));
        //then
        assertEquals(EMPLOYEE_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldReturnListOfEmployeesByIds() {
        //given
        List<Long> ids = List.of(100L);

        Employee employee = new Employee();
        List<Employee> employees = new ArrayList<>();
        employees.add(employee);
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findByEmployeeIds(ids)).thenReturn(employees);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        //when
        List<EmployeeDto> result = testSubject.findByIds(ids);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findByEmployeeIds(ids);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void shouldUpdateExistedEmployee() {
        //given
        Long id = 100L;
        Employee employee = new Employee();
        employee.setId(id);
        Department department = new Department();
        department.setId(id);
        Position position = new Position();
        position.setId(id);
        employee.setDepartment(department);
        employee.setPosition(position);

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(id);
        PositionDto positionDto = new PositionDto();
        positionDto.setId(id);

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(id);
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        employeeDto.setBossId(999L);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(employee.getDepartment().getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(employee.getPosition().getId())).thenReturn(Optional.of(position));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);
        when(employeeRepository.findById(employeeDto.getBossId())).thenReturn(Optional.of(employee));
        //when
        EmployeeDto result = testSubject.updateEmployee(employeeDto);
        //then
        assertNotNull(result);
        verify(employeeRepository, times(1)).findById(employeeDto.getId());
        verify(departmentRepository, times(1)).findById(employee.getDepartment().getId());
        verify(positionRepository, times(1)).findById(employee.getPosition().getId());
        verify(employeeRepository, times(1)).save(employee);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void shouldNotUpdateEmployeeIsNotExist_thenException() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();
        PositionDto positionDto = new PositionDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(EMPLOYEE_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldNotUpdateDepartmentInEmployeeIsNotExist_thenException() {
        //given
        Long id = 100L;
        DepartmentDto departmentDto = new DepartmentDto();
        PositionDto positionDto = new PositionDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(id);
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        Employee employee = new Employee();
        Department department = new Department();
        department.setId(id);
        employee.setDepartment(department);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(DEPARTMENT_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldNotUpdatePositionInEmployeeIsNotExist_thenException() {
        //given
        Long id = 100L;
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(id);
        PositionDto positionDto = new PositionDto();
        positionDto.setId(id);
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(id);
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        Employee employee = new Employee();
        Department department = new Department();
        department.setId(id);
        Position position = new Position();
        position.setId(id);
        employee.setPosition(position);
        employee.setDepartment(department);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(POSITION_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldNotUpdateDepartmentDtoInEmployeeIsNull_thenException() {
        //given
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setDepartmentDto(null);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(DEPARTMENT_DTO_CAN_NOT_BE_NULL, result.getMessage());
    }

    @Test
    void shouldNotUpdatePositionDtoInEmployeeIsNull_thenException() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(null);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(POSITION_DTO_CAN_NOT_BE_NULL, result.getMessage());
    }

    @Test
    void shouldNotUpdateEmployeeCanNotBeBossWithTheSameId_thenException() {
        //given
        Long id = 100L;
        Department department = new Department();
        Position position = new Position();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setPosition(position);
        employee.setDepartment(department);

        EmployeeDto employeeDto = new EmployeeDto();
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(id);
        PositionDto positionDto = new PositionDto();
        positionDto.setId(id);

        employeeDto.setId(id);
        employeeDto.setBossId(id);
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(positionRepository.findById(id)).thenReturn(Optional.of(position));
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        //when
        RuntimeException result = assertThrows(RuntimeException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(EMPLOYEE_CAN_NOT_BE_BOSS_WITH_THE_SAME_ID, result.getMessage());
    }

    @Test
    void shouldNotUpdateEmployeeBossIsNotExist_thenException() {
        Long id = 100L;
        Long bossId = 999L;
        Department department = new Department();
        Position position = new Position();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setPosition(position);
        employee.setDepartment(department);

        EmployeeDto employeeDto = new EmployeeDto();
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(id);
        PositionDto positionDto = new PositionDto();
        positionDto.setId(id);

        employeeDto.setId(id);
        employeeDto.setBossId(bossId);
        employeeDto.setDepartmentDto(departmentDto);
        employeeDto.setPositionDto(positionDto);
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(positionRepository.findById(id)).thenReturn(Optional.of(position));
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(employeeDto.getBossId())).thenReturn(Optional.empty());
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateEmployee(employeeDto));
        //then
        assertEquals(EMPLOYEE_BOSS_DOES_NOT_EXIST, result.getMessage());
    }
}