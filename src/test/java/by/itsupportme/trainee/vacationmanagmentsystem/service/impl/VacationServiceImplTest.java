package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.VacationMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.VacationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class VacationServiceImplTest {

    @Mock
    VacationRepository vacationRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    VacationMapper vacationMapper;

    @InjectMocks
    VacationServiceImpl testSubject;

    @Test
    void shouldSaveCurrentVacation() {
        //given
        Long id = 100L;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateStart = currentDate.plusDays(3);
        LocalDate dateEnd = dateStart.plusDays(17);

        VacationDto vacationDto = new VacationDto();
        vacationDto.setId(id);
        vacationDto.setDateStart(dateStart);
        vacationDto.setDateEnd(dateEnd);

        Vacation vacation = new Vacation();
        vacation.setId(id);

        Employee employee = new Employee();
        employee.setId(id);

        vacation.setEmployee(employee);

        when(vacationMapper.toDto(vacation)).thenReturn(vacationDto);
        when(vacationMapper.toEntity(vacationDto)).thenReturn(vacation);
        when(vacationRepository.save(vacation)).thenReturn(vacation);
        when(employeeRepository.findById(vacationDto.getEmployeeId())).thenReturn(Optional.of(employee));
        //when
        VacationDto result = testSubject.saveVacation(vacationDto);
        //then
        assertNotNull(result);
        verify(vacationMapper, times(1)).toDto(vacation);
        verify(vacationMapper, times(1)).toEntity(vacationDto);
        verify(vacationRepository, times(1)).save(vacation);
        verify(employeeRepository, times(1)).findById(vacationDto.getEmployeeId());
    }

    @Test
    void shouldNotSaveVacation_thenException() {
        //given
        VacationDto vacationDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveVacation(vacationDto));
        //then
        assertEquals(VACATION_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldNotSaveVacationDateStartNotExist_thenException() {
        //given
        VacationDto vacationDto = new VacationDto();
        vacationDto.setDateStart(null);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveVacation(vacationDto));
        //then
        assertEquals(CAN_NOT_TAKE_VACATION_FROM_THIS_DATE, result.getMessage());
    }

    @Test
    void shouldNotSaveVacationDateEndNotExist_thenException() {
        //given
        VacationDto vacationDto = new VacationDto();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateStart = currentDate.plusDays(3);
        vacationDto.setDateStart(dateStart);
        vacationDto.setDateEnd(null);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveVacation(vacationDto));
        //then
        assertEquals(CAN_NOT_TAKE_VACATION_TO_THIS_DATE, result.getMessage());
    }

    @Test
    void shouldNotSaveVacationDateStartAndDateEndNotExist_thenException() {
        //given
        VacationDto vacationDto = new VacationDto();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateStart = currentDate.plusDays(3);
        LocalDate dateEnd = currentDate.minusDays(5);
        vacationDto.setDateStart(dateStart);
        vacationDto.setDateEnd(dateEnd);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveVacation(vacationDto));
        //then
        assertEquals(CAN_NOT_TAKE_VACATION_ON_THIS_DATE, result.getMessage());
    }

    @Test
    void shouldNotSaveVacationEmployeeNotExist_thenException() {
        //given
        Long id = 100L;
        VacationDto vacationDto = new VacationDto();
        vacationDto.setId(id);
        Employee employee = new Employee();
        employee.setId(id);
        vacationDto.setEmployeeId(id);

        LocalDate currentDate = LocalDate.now();
        LocalDate dateStart = currentDate.plusDays(3);
        LocalDate dateEnd = dateStart.plusDays(17);
        vacationDto.setDateStart(dateStart);
        vacationDto.setDateEnd(dateEnd);

        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.saveVacation(vacationDto));
        //then
        assertEquals(EMPLOYEE_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldGetCurrentVacationById() {
        //given
        Long id = 100L;
        Vacation vacation = new Vacation();
        vacation.setId(id);

        VacationDto vacationDto = new VacationDto();

        when(vacationRepository.findById(id)).thenReturn(Optional.of(vacation));
        when(vacationMapper.toDto(vacation)).thenReturn(vacationDto);
        //when
        VacationDto result = testSubject.findById(id);
        //then
        assertNotNull(result);
        verify(vacationRepository, times(1)).findById(id);
        verify(vacationMapper, times(1)).toDto(vacation);
    }

    @Test
    void shouldNotGetCurrentVacationById_thenException() {
        //given
        Long id = 100L;
        Vacation vacation = new Vacation();
        vacation.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.findById(id));
        //then
        assertEquals(VACATION_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldGetAllCurrentVacations() {
        //given
        VacationDto vacationDto = new VacationDto();

        Vacation vacation = new Vacation();
        when(vacationRepository.findAllVacations()).thenReturn(Collections.singletonList(vacation));
        when(vacationMapper.toDto(vacation)).thenReturn(vacationDto);
        //when
        List<VacationDto> result = testSubject.getAllVacations();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(vacationRepository, times(1)).findAllVacations();
        verify(vacationMapper, times(1)).toDto(vacation);
    }

    @Test
    void shouldGetEmptyListWhenVacationsNotExists() {
        //given
        when(vacationRepository.findAllVacations()).thenReturn(Collections.emptyList());
        //when
        List<VacationDto> result = testSubject.getAllVacations();
        //then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(vacationRepository, times(1)).findAllVacations();
    }

    @Test
    void shouldDeleteExistedVacation() {
        //given
        Long id = 100L;
        Vacation vacation = new Vacation();
        vacation.setId(id);
        when(vacationRepository.findById(id)).thenReturn(Optional.of(vacation));
        //when
        testSubject.deleteVacation(id);
        //then
        verify(vacationRepository, times(1)).findById(id);
        verify(vacationRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldNotDeleteVacation_thenException() {
        //given
        Long id = 100L;
        Vacation vacation = new Vacation();
        vacation.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.deleteVacation(id));
        //then
        assertEquals(VACATION_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldUpdateExistedVacation() {
        //given
        Long id = 100L;
        Vacation vacation = new Vacation();
        Employee employee = new Employee();
        employee.setId(id);
        vacation.setEmployee(employee);

        LocalDate currentDate = LocalDate.now();
        LocalDate dateStart = currentDate.plusDays(3);
        LocalDate dateEnd = dateStart.plusDays(17);

        VacationDto vacationDto = new VacationDto();
        vacationDto.setDateStart(dateStart);
        vacationDto.setDateEnd(dateEnd);

        when(vacationRepository.findById(vacationDto.getId())).thenReturn(Optional.of(vacation));
        when(vacationRepository.save(vacation)).thenReturn(vacation);
        when(vacationMapper.toDto(vacation)).thenReturn(vacationDto);
        //when
        VacationDto result = testSubject.updateVacation(vacationDto);
        //then
        assertNotNull(result);
        verify(vacationRepository, times(1)).findById(vacationDto.getId());
        verify(vacationRepository, times(1)).save(vacation);
        verify(vacationMapper, times(1)).toDto(vacation);
    }

    @Test
    void shouldNotUpdateVacationIsNotExist_thenException() {
        //given
        VacationDto vacationDto = new VacationDto();
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updateVacation(vacationDto));
        //then
        assertEquals(VACATION_DOES_NOT_EXIST, result.getMessage());
    }
}