package by.itsupportme.trainee.vacationmanagmentsystem.service;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Status;

import java.util.List;

public interface VacationService {
    VacationDto saveVacation(VacationDto vacationDto);
    List<VacationDto> getAllVacations();
    void deleteVacation(Long id);
    VacationDto updateVacation(VacationDto vacationDto);
    VacationDto findById(Long id);
    List<VacationDto> findAllVacations(Long id, Status status);
    List<VacationDto> getPageWithVacations(int pageNo);
}
