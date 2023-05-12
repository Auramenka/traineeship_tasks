package by.itsupportme.trainee.vacationmanagmentsystem.controller;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    private final VacationService vacationService;

    @PostMapping
    public ResponseEntity<VacationDto> createVacation(@RequestBody VacationDto vacationDto) {
        return new ResponseEntity<>(vacationService.saveVacation(vacationDto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<VacationDto> getAllVacations() {
        return vacationService.getAllVacations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacationDto> getPositionById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(vacationService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVacation(@PathVariable("id") Long id) {
        vacationService.deleteVacation(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<VacationDto> updateVacation(@RequestBody VacationDto vacationDto) {
        return new ResponseEntity<>(vacationService.updateVacation(vacationDto), HttpStatus.OK);
    }
}