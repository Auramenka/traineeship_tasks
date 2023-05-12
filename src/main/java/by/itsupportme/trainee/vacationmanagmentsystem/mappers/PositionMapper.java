package by.itsupportme.trainee.vacationmanagmentsystem.mappers;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionDto toDto(Position position);
    Position toEntity(PositionDto positionDto);
}
