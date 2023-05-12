package by.itsupportme.trainee.vacationmanagmentsystem.integration;

import by.itsupportme.trainee.vacationmanagmentsystem.VacationManagmentSystemApplicationTests;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

class DepartmentIntegrationTest extends VacationManagmentSystemApplicationTests {

    private static final String TEST_DEPARTMENT = "Test department";
    private static final String UPDATE_DEPARTMENT = "Update department";

    @Autowired
    private DepartmentRepository departmentRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanUpDB() {
        departmentRepository.deleteAll();
    }

    DepartmentDto createDepartmentDto(String name) {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setName(name);
        return departmentDto;
    }

    @Test
    void shouldSaveCurrentDepartment() throws Exception {
        DepartmentDto departmentDto = createDepartmentDto(TEST_DEPARTMENT);
        String json = objectMapper.writeValueAsString(departmentDto);
        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(departmentDto.getName()));
    }

    @Test
    void shouldGetAllCurrentDepartments() throws Exception {
        createDepartment(TEST_DEPARTMENT);
        mockMvc.perform(get("/api/departments")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldGetEmptyListWhenDepartmentsNotExists() throws Exception {
        createDepartmentDto("");
        ResultActions actions =  mockMvc.perform(get("/api/departments"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetByIdDepartment() throws Exception {
        Long id = createDepartment(TEST_DEPARTMENT);
        mockMvc.perform(get("/api/departments/{id}", id)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByIdWhenDepartmentNotExists_thenException() throws Exception {
        mockMvc.perform(get("/api/departments/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEPARTMENT_DOES_NOT_EXIST));
    }

    @Test
    void shouldDeleteExistedDepartment() throws Exception {
        Long id = createDepartment(TEST_DEPARTMENT);
        mockMvc.perform(delete("/api/departments/{id}" , id)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteDepartment_thenException() throws Exception {
        mockMvc.perform(delete("/api/departments/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEPARTMENT_DOES_NOT_EXIST));
    }

    @Test
    void shouldUpdateExistedDepartment() throws Exception {
        Long id = createDepartment(TEST_DEPARTMENT);
        DepartmentDto departmentDto = createDepartmentDto(UPDATE_DEPARTMENT);
        departmentDto.setId(id);
        String json = objectMapper.writeValueAsString(departmentDto);
        mockMvc.perform(put("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(departmentDto.getName()));
        mockMvc.perform(get("/api/departments/{id}", id)
        )
                .andExpect(jsonPath("$.name").value(departmentDto.getName()));
    }

    @Test
    void shouldNotUpdateDepartmentIsNotExist_thenException() throws Exception {
        DepartmentDto departmentDto = createDepartmentDto(TEST_DEPARTMENT);
        departmentDto.setId(999L);
        String json = objectMapper.writeValueAsString(departmentDto);
        mockMvc.perform(put("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEPARTMENT_DOES_NOT_EXIST));
    }

    private Long createDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        return departmentRepository.save(department).getId();
    }
}
