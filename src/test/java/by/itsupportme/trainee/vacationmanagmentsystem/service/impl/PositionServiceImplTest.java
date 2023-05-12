package by.itsupportme.trainee.vacationmanagmentsystem.service.impl;

import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.exception.NotExistsException;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.PositionMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    PositionRepository positionRepository;

    @Mock
    PositionMapper positionMapper;

    @InjectMocks
    PositionServiceImpl testSubject;

    @Test
    void shouldSaveCurrentPosition() {
        //given
        PositionDto positionDto = new PositionDto();
        Position position = new Position();
        when(positionMapper.toEntity(positionDto)).thenReturn(position);
        when(positionRepository.save(position)).thenReturn(position);
        when(positionMapper.toDto(position)).thenReturn(positionDto);
        //when
        PositionDto result = testSubject.savePosition(positionDto);
        //then
        assertNotNull(result);
        verify(positionMapper, times(1)).toEntity(positionDto);
        verify(positionRepository, times(1)).save(position);
        verify(positionMapper, times(1)).toDto(position);
    }

    @Test
    void shouldNotSavePosition_thenException() {
        //given
        PositionDto positionDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.savePosition(positionDto));
        //then
        assertEquals(POSITION_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldGetCurrentPositionById() {
        //given
        Long id = 100L;
        Position position = new Position();
        position.setId(id);

        PositionDto positionDto = new PositionDto();

        when(positionRepository.findById(id)).thenReturn(Optional.of(position));
        when(positionMapper.toDto(position)).thenReturn(positionDto);
        //when
        PositionDto result = testSubject.findById(id);
        //then
        assertNotNull(result);
        verify(positionRepository, times(1)).findById(id);
        verify(positionMapper, times(1)).toDto(position);
    }

    @Test
    void shouldNotGetCurrentPositionById_thenException() {
        //given
        Long id = 100L;
        Position position = new Position();
        position.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.findById(id));
        //then
        assertEquals(POSITION_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldGetAllCurrentPositions() {
        //given
        PositionDto positionDto = new PositionDto();

        Position position = new Position();
        when(positionRepository.findAll()).thenReturn(Collections.singletonList(position));
        when(positionMapper.toDto(position)).thenReturn(positionDto);
        //when
        List<PositionDto> result = testSubject.getAllPositions();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(positionRepository, times(1)).findAll();
        verify(positionMapper, times(1)).toDto(position);
    }

    @Test
    void shouldGetEmptyListWhenPositionsNotExists() {
        //given
        when(positionRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<PositionDto> result = testSubject.getAllPositions();
        //then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void shouldDeleteExistedPosition() {
        //given
        Long id = 100L;
        Position position = new Position();
        position.setId(id);

        when(positionRepository.findById(id)).thenReturn(Optional.of(position));
        //when
        testSubject.deletePosition(id);
        //then
        verify(positionRepository, times(1)).findById(id);
        verify(positionRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldNotDeletePosition_thenException() {
        //given
        Long id = 100L;
        Position position = new Position();
        position.setId(id);
        //when
        NotExistsException result = assertThrows(NotExistsException.class,() -> testSubject.deletePosition(id));
        //then
        assertEquals(POSITION_DOES_NOT_EXIST, result.getMessage());
    }

    @Test
    void shouldUpdateExistedPosition() {
        //given
        PositionDto positionDto = new PositionDto();

        Position position = new Position();
        when(positionRepository.findById(positionDto.getId())).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);
        when(positionMapper.toDto(position)).thenReturn(positionDto);
        //when
        PositionDto result = testSubject.updatePosition(positionDto);
        //then
        assertNotNull(result);
        verify(positionRepository, times(1)).findById(positionDto.getId());
        verify(positionRepository, times(1)).save(position);
        verify(positionMapper, times(1)).toDto(position);
    }

    @Test
    void shouldNotUpdatePositionDtoIsEmpty_thenException() {
        //given
        PositionDto positionDto = null;
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updatePosition(positionDto));
        //then
        assertEquals(POSITION_DTO_IS_EMPTY, result.getMessage());
    }

    @Test
    void shouldNotUpdatePositionIsNotExist_thenException() {
        //given
        PositionDto positionDto = new PositionDto();
        //when
        NotExistsException result = assertThrows(NotExistsException.class, () -> testSubject.updatePosition(positionDto));
        //then
        assertEquals(POSITION_DOES_NOT_EXIST, result.getMessage());
    }
}