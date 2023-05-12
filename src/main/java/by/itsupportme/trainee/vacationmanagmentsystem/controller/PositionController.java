package by.itsupportme.trainee.vacationmanagmentsystem.controller;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(@RequestBody PositionDto positionDTO) {
        return new ResponseEntity<>(positionService.savePosition(positionDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public List<PositionDto> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(positionService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePosition(@PathVariable("id") Long id) {
        positionService.deletePosition(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<PositionDto> updatePosition(@RequestBody PositionDto positionDto) {
        return new ResponseEntity<>(positionService.updatePosition(positionDto), HttpStatus.OK);
    }
}
