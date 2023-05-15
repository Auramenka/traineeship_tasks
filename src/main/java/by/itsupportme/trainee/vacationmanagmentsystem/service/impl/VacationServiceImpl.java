package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.VacationMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Status;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.VacationRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new NotExistsException("VacationDto is empty");
        }

        checkDateOfVacation(vacationDto);

        takeVacationOnThisDate(vacationDto);

        Employee employee = employeeRepository.findById(vacationDto.getEmployeeId())
                .orElseThrow(() -> new NotExistsException("Employee doesn't exist"));

        Vacation vacation = vacationMapper.toEntity(vacationDto);
        vacation.setEmployee(employee);
        return vacationMapper.toDto(vacationRepository.save(vacation));
    }

    public List<VacationDto> getAllVacations() {
        return vacationRepository.findAllVacations().stream()
                .map(vacationMapper::toDto)
                .collect(Collectors.toList());
    }

    public VacationDto findById(Long id) {
        return vacationMapper.toDto(getVacationFromDB(id));
    }

    public List<VacationDto> findAllVacations(Long id, Status status) {
        return vacationRepository.findAllMyVacationsAndBoss(id, status).stream()
                .map(vacationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteVacation(Long id) {
        Vacation vacationFromDB = getVacationFromDB(id);
        vacationRepository.deleteById(vacationFromDB.getId());
    }

    public VacationDto updateVacation(VacationDto vacationDto) {
        Vacation vacationFromDB = getVacationFromDB(vacationDto.getId());

        checkDateOfVacation(vacationDto);

        takeVacationOnThisDate(vacationDto);

        vacationFromDB.setDateStart(vacationDto.getDateStart());
        vacationFromDB.setDateEnd(vacationDto.getDateEnd());
        vacationFromDB.setStatus(vacationDto.getStatus());

        return vacationMapper.toDto(vacationRepository.save(vacationFromDB));
    }

    private void checkDateOfVacation(VacationDto vacationDto) {
        if (vacationDto.getDateStart() == null) {
            throw new NotExistsException("You can't take a vacation from this date");
        }

        if (vacationDto.getDateEnd() == null) {
            throw new NotExistsException("You can't take a vacation to this date");
        }
    }

    private Vacation getVacationFromDB(Long id) {
        return vacationRepository.findById(id).orElseThrow(() -> new NotExistsException("Vacation doesn't exist"));
    }

    private void takeVacationOnThisDate(VacationDto vacationDto) {
        if (vacationDto.getDateStart().isAfter(LocalDate.now())
                && vacationDto.getDateEnd().isAfter(vacationDto.getDateStart()) == false) {
            throw new NotExistsException("You can't take a vacation on this date");
        }
    }
}
