package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.PositionMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionDto savePosition(PositionDto positionDto) {
        checkPositionDto(positionDto);
        return positionMapper.toDto(positionRepository.save(positionMapper.toEntity(positionDto)));
    }

    public PositionDto findById(Long id) {
        return positionMapper.toDto(getPositionFromDB(id));
    }

    public List<PositionDto> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deletePosition(Long id) {
        Position positionFromDB = getPositionFromDB(id);
        positionRepository.deleteById(positionFromDB.getId());
    }

    public PositionDto updatePosition(PositionDto positionDto) {
        checkPositionDto(positionDto);
        Position positionFromDB = getPositionFromDB(positionDto.getId());
        positionFromDB.setName(positionDto.getName());
        return positionMapper.toDto(positionRepository.save(positionFromDB));
    }

    private void checkPositionDto(PositionDto positionDto) {
        if (positionDto == null) {
            throw new NotExistsException("PositionDto is empty");
        }
    }

    private Position getPositionFromDB(Long id) {
        return positionRepository.findById(id).orElseThrow(() -> new NotExistsException("Position doesn't exist"));
    }
}