package by.itsupportme.trainee.vacationmanagmentsystem.integration;

import by.itsupportme.trainee.vacationmanagmentsystem.VacationManagmentSystemApplicationTests;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.DepartmentDto;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.EmployeeDto;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.PositionDto;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.DepartmentMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.mappers.PositionMapper;
import by.itsupportme.trainee.vacationmanagmentsystem.model.*;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static by.itsupportme.trainee.vacationmanagmentsystem.constants.Constants.*;

class EmployeeIntegrationTest extends VacationManagmentSystemApplicationTests {

    private static final String TEST_FIRST_NAME = "Test first name";
    private static final String TEST_LAST_NAME = "Test last name";
    private static final String TEST_FIRST_NAME_EMPLOYEE = "Test first name employee";
    private static final String TEST_LAST_NAME_EMPLOYEE = "Test last name employee";
    private static final String UPDATE_FIRST_NAME = "Update first name";
    private static final String UPDATE_LAST_NAME = "Update last name";
    private static final String TEST_NAME_POSITION = "Test name position";
    private static final String UPDATE_NAME_POSITION = "Update name position";
    private static final String TEST_NAME_DEPARTMENT = "Test name department";
    private static final String UPDATE_NAME_DEPARTMENT = "Update name department";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private PositionMapper positionMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanUpDB() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        positionRepository.deleteAll();
    }

    @BeforeEach
    void configureDate() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    EmployeeDto createEmployeeDto (
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            Boolean isFired,
            DepartmentDto departmentDto,
            PositionDto positionDto,
            Long bossId
    ) {
        return EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .isFired(isFired)
                .departmentDto(departmentDto)
                .positionDto(positionDto)
                .bossId(bossId)
                .build();
    }

    @Test
    void shouldSaveCurrentEmployee() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        DepartmentDto departmentDto = departmentMapper.toDto(createDepartment());
        PositionDto positionDto = positionMapper.toDto(createPosition());

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
               dateOfBirth, Gender.WOMAN, false, departmentDto, positionDto, null);

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employeeDto.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(printFormatDate(dateOfBirth)))
                .andExpect(jsonPath("$.departmentDto.id").value(employeeDto.getDepartmentDto().getId()))
                .andExpect(jsonPath("$.positionDto.id").value(employeeDto.getPositionDto().getId()))
                .andExpect(jsonPath("$.bossId").value(employeeDto.getBossId()));
    }

    @Test
    void shouldGetAllCurrentEmployees() throws Exception {
        createEmployee(createDepartment(), createPosition());
        mockMvc.perform(get("/api/employees")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldGetEmptyListWhenEmployeesNotExists() throws Exception {
        createEmployeeDto(null, null, null, null, null, null,
                null, null);
        ResultActions actions = mockMvc.perform(get("/api/employees"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetByIdEmployee() throws Exception {
        Employee employee = createEmployee(createDepartment(), createPosition());
        mockMvc.perform(get("/api/employees/{id}", employee.getId())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByIdWhenEmployeeNotExists_thenException() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMPLOYEE_DOES_NOT_EXIST));
    }

    @Test
    void shouldGetAllEmployeesByIds() throws Exception {
        Employee employee1 = createEmployee(createDepartment(), createPosition());
        Employee employee2 = createEmployee(createDepartment(), createPosition());

        mockMvc.perform(get("/api/employees/list/{ids}", employee1.getId() + "," + employee2.getId())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetEmptyListWhenIdsEmployeesNotExists() throws Exception {
        ResultActions actions = mockMvc.perform(get("/api/employees/list/{ids}",  "100,101"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetEmptyListWhenIdsNotExistsInEmployees() throws Exception {
        createEmployee(createDepartment(), createPosition());
        createEmployee(createDepartment(), createPosition());
        ResultActions actions = mockMvc.perform(get("/api/employees/list/{ids}", "998,999"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldDeleteExistedEmployee() throws Exception {
        Employee employee = createEmployee(createDepartment(), createPosition());
        mockMvc.perform(delete("/api/employees/{id}", employee.getId())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteEmployee_thenException() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMPLOYEE_DOES_NOT_EXIST));
    }

    @Test
    void shouldUpdateExistedEmployee() throws Exception{
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 10, 24));

        Department department = createDepartment();
        Position position = createPosition();

        Employee withoutBoss = createEmployee(department, position);
        Employee employeeWithBoss = createEmployeeWithBoss(withoutBoss, department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);
        DepartmentDto updateDepartment = departmentMapper.toDto(updateDepartment(departmentMapper.toEntity(departmentDto)));
        updateDepartment.setId(departmentDto.getId());

        PositionDto positionDto = positionMapper.toDto(position);
        PositionDto updatePosition = positionMapper.toDto(updatePosition(positionMapper.toEntity(positionDto)));
        updatePosition.setId(positionDto.getId());

        EmployeeDto employeeDto = createEmployeeDto(UPDATE_FIRST_NAME, UPDATE_LAST_NAME,
                dateOfBirth, Gender.MAN, true, updateDepartment, updatePosition,
                withoutBoss.getId());

        employeeDto.setId(employeeWithBoss.getId());

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(employeeDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employeeDto.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(printFormatDate(dateOfBirth)))
                .andExpect(jsonPath("$.departmentDto.id").value(employeeDto.getDepartmentDto().getId()))
                .andExpect(jsonPath("$.positionDto.id").value(employeeDto.getPositionDto().getId()))
                .andExpect(jsonPath("$.bossId").value(withoutBoss.getId()));
    }

    @Test
    void shouldNotUpdateEmployeeIsNotExist_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        DepartmentDto departmentDto = departmentMapper.toDto(createDepartment());
        PositionDto positionDto = positionMapper.toDto(createPosition());

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false,  departmentDto, positionDto, null);
        employeeDto.setId(999L);

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMPLOYEE_DOES_NOT_EXIST));
    }

    @Test
    void shouldNotUpdateEmployeeCanNotBeBossWithTheSameId_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 10, 24));

        Department department = createDepartment();
        Position position = createPosition();

        Employee withoutBoss = createEmployee(department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);
        DepartmentDto updateDepartment = departmentMapper.toDto(updateDepartment(departmentMapper.toEntity(departmentDto)));
        updateDepartment.setId(departmentDto.getId());

        PositionDto positionDto = positionMapper.toDto(position);
        PositionDto updatePosition = positionMapper.toDto(updatePosition(positionMapper.toEntity(positionDto)));
        updatePosition.setId(positionDto.getId());

        EmployeeDto employeeDto = createEmployeeDto(UPDATE_FIRST_NAME, UPDATE_LAST_NAME,
                dateOfBirth, Gender.MAN, true, updateDepartment, updatePosition,
                withoutBoss.getId());

        employeeDto.setId(withoutBoss.getId());

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMPLOYEE_CAN_NOT_BE_BOSS_WITH_THE_SAME_ID));
    }

    @Test
    void shouldNotUpdateEmployeeBossIsNotExist_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        Department department = createDepartment();
        Position position = createPosition();

        Employee employee = createEmployee(department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);
        PositionDto positionDto = positionMapper.toDto(position);

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false,  departmentDto, positionDto, 100L);
        employeeDto.setId(employee.getId());

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMPLOYEE_BOSS_DOES_NOT_EXIST));
    }

    @Test
    void shouldNotUpdateEmployeeDepartmentDtoIsNull_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        PositionDto positionDto = positionMapper.toDto(createPosition());

        Employee employee = createEmployee(createDepartment(), createPosition());

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false, null, positionDto, null);
        employeeDto.setId(employee.getId());

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEPARTMENT_DTO_CAN_NOT_BE_NULL));
    }

    @Test
    void shouldNotUpdateEmployeePositionDtoIsNull_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        Department department = createDepartment();
        Position position = createPosition();

        Employee employee = createEmployee(department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false, departmentDto, null, null);
        employeeDto.setId(employee.getId());

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(POSITION_DTO_CAN_NOT_BE_NULL));
    }

    @Test
    void shouldNotUpdateEmployeeDepartmentIsNotExist_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        Department department = createDepartment();
        Position position = createPosition();

        Employee employee = createEmployee(department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);
        PositionDto positionDto = positionMapper.toDto(position);

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false, departmentDto, positionDto, null);
        employeeDto.setId(employee.getId());

        employeeDto.getDepartmentDto().setId(999L);

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEPARTMENT_DOES_NOT_EXIST));
    }

    @Test
    void shouldNotUpdateEmployeePositionIsNotExist_thenException() throws Exception {
        LocalDate dateOfBirth = createDate(LocalDate.of(1997, 04, 18));

        Department department = createDepartment();
        Position position = createPosition();

        Employee employee = createEmployee(department, position);

        DepartmentDto departmentDto = departmentMapper.toDto(department);
        PositionDto positionDto = positionMapper.toDto(position);

        EmployeeDto employeeDto = createEmployeeDto(TEST_FIRST_NAME, TEST_LAST_NAME,
                dateOfBirth, Gender.WOMAN, false, departmentDto, positionDto, null);
        employeeDto.setId(employee.getId());

        employeeDto.getPositionDto().setId(999L);

        String json = objectMapper.writeValueAsString(employeeDto);
        mockMvc.perform(put("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(POSITION_DOES_NOT_EXIST));
    }

    private LocalDate createDate(LocalDate date) {
        return date;
    }

    private String printFormatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private Position createPosition() {
        Position position = new Position();
        position.setName(TEST_NAME_POSITION);
        return positionRepository.save(position);
    }

    private Position updatePosition(Position position) {
        position.setName(UPDATE_NAME_POSITION);
        return position;
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setName(TEST_NAME_DEPARTMENT);
        return departmentRepository.save(department);
    }

    private Department updateDepartment(Department department) {
        department.setName(UPDATE_NAME_DEPARTMENT);
        return department;
    }

    private Employee createEmployeeWithBoss(Employee boss, Department department, Position position) {
        Employee employee = createEmployee(department, position);
        employee.setBoss(boss);
        return employeeRepository.save(employee);
    }

    private Employee createEmployee(Department department, Position position) {
        LocalDate dateOfBirth =  LocalDate.of(1997, 04, 18);
        Employee employee = new Employee();
        employee.setFirstName(TEST_FIRST_NAME_EMPLOYEE);
        employee.setLastName(TEST_LAST_NAME_EMPLOYEE);
        employee.setGender(Gender.MAN);
        employee.setDateOfBirth(dateOfBirth);
        employee.setIsFired(false);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setBoss(null);
        return employeeRepository.save(employee);
    }
}
