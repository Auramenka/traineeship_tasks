package by.itsupportme.trainee.vacationmanagmentsystem.integration;

import by.itsupportme.trainee.vacationmanagmentsystem.VacationManagmentSystemApplicationTests;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PositionIntegrationTest extends VacationManagmentSystemApplicationTests {

    private static final String TEST_POSITION = "Test position";
    private static final String POSITION_DOES_NOT_EXIST = "Position doesn't exist";

    @Autowired
    private PositionRepository positionRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanUpDB() {
        positionRepository.deleteAll();
    }

    PositionDto createPositionDto(String name) {
        PositionDto positionDto = new PositionDto();
        positionDto.setName(name);
        return positionDto;
    }

    @Test
    void shouldSaveCurrentPosition() throws Exception {
        PositionDto positionDto = createPositionDto(TEST_POSITION);
        String json = objectMapper.writeValueAsString(positionDto);
        mockMvc.perform(post("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(positionDto.getName()));
    }

    @Test
    void shouldGetAllCurrentPositions() throws Exception {
        createPosition(TEST_POSITION);
        mockMvc.perform(get("/api/positions")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldGetEmptyListWhenPositionsNotExists() throws Exception {
        createPositionDto("");
        ResultActions actions =  mockMvc.perform(get("/api/positions"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetByIdPosition() throws Exception {
        Long id = createPosition(TEST_POSITION);
        mockMvc.perform(get("/api/positions/{id}", id)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByIdWhenPositionNotExists_thenException() throws Exception {
        mockMvc.perform(get("/api/positions/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(POSITION_DOES_NOT_EXIST));
    }

    @Test
    void shouldDeleteExistedPosition() throws Exception {
        Long id = createPosition(TEST_POSITION);
        mockMvc.perform(delete("/api/positions/{id}" , id)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeletePosition_thenException() throws Exception {
        createPosition(TEST_POSITION);
        mockMvc.perform(delete("/api/positions/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(POSITION_DOES_NOT_EXIST));
    }

    @Test
    void shouldUpdateExistedPosition() throws Exception {
        Long id = createPosition(TEST_POSITION);
        PositionDto positionDto = createPositionDto("Update position");
        positionDto.setId(id);
        String json = objectMapper.writeValueAsString(positionDto);
        mockMvc.perform(put("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(positionDto.getName()));
        mockMvc.perform(get("/api/positions/{id}", id)
        )
                .andExpect(jsonPath("$.name").value(positionDto.getName()));
    }

    @Test
    void shouldNotUpdatePositionIsNotExist_thenException() throws Exception {
        PositionDto positionDto = createPositionDto(TEST_POSITION);
        positionDto.setId(999L);
        String json = objectMapper.writeValueAsString(positionDto);
        mockMvc.perform(put("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(POSITION_DOES_NOT_EXIST));
    }

    private Long createPosition(String name) {
        Position position = new Position();
        position.setName(name);
        return positionRepository.save(position).getId();
    }
}
