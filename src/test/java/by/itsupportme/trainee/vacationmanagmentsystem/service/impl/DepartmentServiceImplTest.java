package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.DepartmentMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    private static final String TEST_NAME = "Test name";

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    DepartmentMapper departmentMapper;

    @InjectMocks
    DepartmentServiceImpl testSubject;

    @Test
    void shouldSaveCurrentDepartment() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setName(TEST_NAME);

        Department department = new Department();
        when(departmentMapper.toEntity(departmentDto)).thenReturn(department);
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);
        when(departmentRepository.save(department)).thenReturn(department);
        //when
        DepartmentDto result = testSubject.saveDepartment(departmentDto);
        //then
        assertNotNull(result);
        assertEquals(TEST_NAME, result.getName());
        verify(departmentMapper, times(1)).toDto(department);
        verify(departmentMapper, times(1)).toEntity(departmentDto);
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void shouldNotSaveDepartment_thenException() {
        //given
        DepartmentDto departmentDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveDepartment(departmentDto));
        //then
        assertEquals(DEPARTMENT_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldGetAllCurrentDepartments() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();

        Department department = new Department();
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);
        //when
        List<DepartmentDto> result = testSubject.getAllDepartments();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(departmentMapper, times(1)).toDto(department);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void shouldGetEmptyListWhenDepartmentsNotExists() {
        //given
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<DepartmentDto> result = testSubject.getAllDepartments();
        //then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCurrentDepartmentById() {
        //given
        Long id = 100L;
        Department department = new Department();
        department.setId(id);

        DepartmentDto departmentDto = new DepartmentDto();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);
        //when
        DepartmentDto result = testSubject.findById(id);
        //then
        assertNotNull(result);
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentMapper, times(1)).toDto(department);
    }

    @Test
    void shouldNotGetCurrentDepartmentById_thenException() {
        //given
        Long id = 100L;
        Department department = new Department();
        department.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.findById(id));
        //then
        assertEquals(DEPARTMENT_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldDeleteExistedDepartment() {
        //given
        Long id = 100L;
        Department department = new Department();
        department.setId(id);

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        //when
        testSubject.deleteDepartment(id);
        //then
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldNotDeleteDepartment_thenException() {
        //given
        Long id = 100L;
        Department department = new Department();
        department.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.deleteDepartment(id));
        //then
        assertEquals(DEPARTMENT_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldUpdateExistedDepartment() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();

        Department department = new Department();
        when(departmentRepository.findById(departmentDto.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);
        //when
        DepartmentDto result = testSubject.updateDepartment(departmentDto);
        //then
        assertNotNull(result);
        verify(departmentRepository, times(1)).findById(departmentDto.getId());
        verify(departmentRepository, times(1)).save(department);
        verify(departmentMapper, times(1)).toDto(department);
    }

    @Test
    void shouldNotUpdateDepartmentDtoIsEmpty_thenException() {
        //given
        DepartmentDto departmentDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateDepartment(departmentDto));
        //then
        assertEquals(DEPARTMENT_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldNotUpdateDepartmentIsNotExist_thenException() {
        //given
        DepartmentDto departmentDto = new DepartmentDto();
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateDepartment(departmentDto));
        //then
        assertEquals(DEPARTMENT_DOES_NOT_EXIST, result.getMessage());
    }
}