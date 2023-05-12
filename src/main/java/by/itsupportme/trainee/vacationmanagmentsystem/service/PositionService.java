package by.itsupportme.trainee.vacationmanagmentsystem.service;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;

import java.util.List;

public interface PositionService {
    PositionDto savePosition(PositionDto positionDto);
    List<PositionDto> getAllPositions();
    void deletePosition(Long id);
    PositionDto updatePosition(PositionDto positionDto);
    PositionDto findById(Long id);
}
