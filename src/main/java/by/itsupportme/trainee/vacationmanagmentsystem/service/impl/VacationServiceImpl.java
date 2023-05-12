package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.VacationMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.VacationRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationMapper vacationMapper;

    public VacationDto saveVacation(VacationDto vacationDto) {

        if (vacationDto == null) {
            throw new NotExistsException(VACATION_DTO_IS_EMPTY);
        }

        checkDateOfVacation(vacationDto);

        if (vacationDto.getDateStart().isAfter(LocalDate.now())
                && vacationDto.getDateEnd().isAfter(vacationDto.getDateStart())) {

            Employee employee = employeeRepository.findById(vacationDto.getEmployeeId())
                    .orElseThrow(() -> new NotExistsException(EMPLOYEE_DOES_NOT_EXIST));

             Vacation vacation = vacationMapper.toEntity(vacationDto);
             vacation.setEmployee(employee);
             return vacationMapper.toDto(vacationRepository.save(vacation));
        } else {
            throw new NotExistsException(CAN_NOT_TAKE_VACATION_ON_THIS_DATE);
        }
    }

    public List<VacationDto> getAllVacations() {
        return vacationRepository.findAllVacations().stream()
                .map(vacationMapper::toDto)
                .collect(Collectors.toList());
    }

    public VacationDto findById(Long id) {
        Vacation vacationFromDb = vacationRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(VACATION_DOES_NOT_EXIST));
        return vacationMapper.toDto(vacationFromDb);
    }

    public void deleteVacation(Long id) {
        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(VACATION_DOES_NOT_EXIST));
        vacationRepository.deleteById(vacation.getId());
    }

    public VacationDto updateVacation(VacationDto vacationDto) {
        Vacation vacationFromDB = vacationRepository.findById(vacationDto.getId())
                .orElseThrow(() -> new NotExistsException(VACATION_DOES_NOT_EXIST));

        checkDateOfVacation(vacationDto);

        if (vacationDto.getDateStart().isAfter(LocalDate.now())
                && vacationDto.getDateEnd().isAfter(vacationDto.getDateStart())) {

            vacationFromDB.setDateStart(vacationDto.getDateStart());
            vacationFromDB.setDateEnd(vacationDto.getDateEnd());
            vacationFromDB.setStatus(vacationDto.getStatus());

            return vacationMapper.toDto(vacationRepository.save(vacationFromDB));
        } else {
            throw new NotExistsException(CAN_NOT_TAKE_VACATION_ON_THIS_DATE);
        }
    }

    private void checkDateOfVacation(VacationDto vacationDto) {
        if (vacationDto.getDateStart() == null) {
            throw new NotExistsException(CAN_NOT_TAKE_VACATION_FROM_THIS_DATE);
        }

        if (vacationDto.getDateEnd() == null) {
            throw new NotExistsException(CAN_NOT_TAKE_VACATION_TO_THIS_DATE);
        }
    }
}
