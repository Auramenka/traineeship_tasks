package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.PositionMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

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
        Position positionFromDb = positionRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(POSITION_DOES_NOT_EXIST));
        return positionMapper.toDto(positionFromDb);
    }

    public List<PositionDto> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deletePosition(Long id) {
        Position positionOptional = positionRepository.findById(id)
                .orElseThrow(() -> new NotExistsException(POSITION_DOES_NOT_EXIST));
        positionRepository.deleteById(positionOptional.getId());
    }

    public PositionDto updatePosition(PositionDto positionDto) {
        checkPositionDto(positionDto);
        Position positionFromDB = positionRepository.findById(positionDto.getId())
                .orElseThrow(() -> new NotExistsException(POSITION_DOES_NOT_EXIST));
        positionFromDB.setName(positionDto.getName());
        return positionMapper.toDto(positionRepository.save(positionFromDB));
    }

    private void checkPositionDto(PositionDto positionDto) {
        if (positionDto == null) {
            throw new NotExistsException(POSITION_DTO_IS_EMPTY);
        }
    }
}