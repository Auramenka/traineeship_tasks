package by.itsupportme.trainee.vacationmanagmentsystem.integration;

import by.itsupportme.trainee.vacationmanagmentsystem.VacationManagmentSystemApplicationTests;
import by.itsupportme.trainee.vacationmanagmentsystem.dto.VacationDto;
import by.itsupportme.trainee.vacationmanagmentsystem.model.*;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.DepartmentRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.EmployeeRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.PositionRepository;
import by.itsupportme.trainee.vacationmanagmentsystem.repository.VacationRepository;
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

class VacationIntegrationTest extends VacationManagmentSystemApplicationTests {

    private static final String TEST_NAME = "Test name";
    private static final String CAN_NOT_TAKE_VACATION_FROM_THIS_DATE = "You can't take a vacation from this date";
    private static final String CAN_NOT_TAKE_VACATION_TO_THIS_DATE = "You can't take a vacation to this date";
    private static final String CAN_NOT_TAKE_VACATION_ON_THIS_DATE = "You can't take a vacation on this date";
    private static final String VACATION_DOES_NOT_EXIST = "Vacation doesn't exist";

    @Autowired
    private VacationRepository vacationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanUpDB() {
        vacationRepository.deleteAll();
        employeeRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @BeforeEach
    void configureDate() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    VacationDto createVacationDto(
            LocalDate dateStart,
            LocalDate dateEnd,
            Long employeeId,
            Status status
    ) {
        return VacationDto.builder()
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .employeeId(employeeId)
                .status(status)
                .build();
    }

    @Test
    void shouldSaveCurrentVacation() throws Exception {
        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(17L);

        Employee employee = createEmployee();

        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, employee.getId(), Status.IN_PROGRESS);

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(post("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.dateStart").value(printFormatDate(dateStart)))
                .andExpect(jsonPath("$.dateEnd").value(printFormatDate(dateEnd)))
                .andExpect(jsonPath("$.employeeId").value(vacationDto.getEmployeeId()))
                .andExpect(jsonPath("$.status").value(vacationDto.getStatus().name()));
    }

    @Test
    void shouldNotSaveVacationDateStartNotExist_thenException() throws Exception {
        LocalDate dateEnd = createVacationDate(17L);
        VacationDto vacationDto = createVacationDto(null, dateEnd, 100L, Status.PENDING_APPROVAL);
        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(post("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_FROM_THIS_DATE));
    }

    @Test
    void shouldNotSaveVacationDateEndNotExist_thenException() throws Exception {
        LocalDate dateStart = createVacationDate(7L);

        VacationDto vacationDto = createVacationDto(dateStart, null, 100L, Status.PENDING_APPROVAL);

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(post("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_TO_THIS_DATE));
    }

    @Test
    void shouldNotSaveVacationDateStartAndDateEndNotExist_thenException() throws Exception {
        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(-10L);

        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, 100L, Status.PENDING_APPROVAL);

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(post("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_ON_THIS_DATE));
    }

    @Test
    void shouldNotSaveVacationEmployeeNotExist_thenException() throws Exception {
        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(17L);

        Long id = 100L;
        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, id, Status.PENDING_APPROVAL);

        Employee employee = new Employee();
        employee.setId(id);
        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(post("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Employee doesn't exist"));
    }

    @Test
    void shouldGetAllCurrentVacations() throws Exception {
        createVacation();
        mockMvc.perform(get("/api/vacations")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldGetEmptyListWhenVacationsNotExists() throws Exception {
        createVacationDto(null, null, null, null);
        ResultActions actions = mockMvc.perform(get("/api/vacations"));
        actions.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetByIdVacation() throws Exception {
        Vacation vacation = createVacation();
        mockMvc.perform(get("/api/vacations/{id}", vacation.getId())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByIdWhenVacationNotExists_thenException() throws Exception {
        mockMvc.perform(get("/api/vacations/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(VACATION_DOES_NOT_EXIST));
    }

    @Test
    void shouldDeleteExistedVacation() throws Exception {
        Vacation vacation = createVacation();
        mockMvc.perform(delete("/api/vacations/{id}" , vacation.getId())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteVacation_thenException() throws Exception {
        mockMvc.perform(delete("/api/vacations/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(VACATION_DOES_NOT_EXIST));
    }

    @Test
    void shouldUpdateExistedVacation() throws Exception {
        Vacation vacation = createVacation();

        LocalDate dateStart = createVacationDate(8L);
        LocalDate dateEnd = createVacationDate(20L);

        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, vacation.getEmployee().getId(), Status.IN_PROGRESS);
        vacationDto.setId(vacation.getId());
        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(put("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.dateStart").value(printFormatDate(dateStart)))
                .andExpect(jsonPath("$.dateEnd").value(printFormatDate(dateEnd)))
                .andExpect(jsonPath("$.employeeId").value(vacation.getEmployee().getId()))
                .andExpect(jsonPath("$.status").value(vacationDto.getStatus().name()));
        mockMvc.perform(get("/api/vacations/{id}", vacation.getId())
        )
                .andExpect(jsonPath("$.dateStart").value(printFormatDate(dateStart)))
                .andExpect(jsonPath("$.dateEnd").value(printFormatDate(dateEnd)))
                .andExpect(jsonPath("$.employeeId").value(vacation.getEmployee().getId()))
                .andExpect(jsonPath("$.status").value(vacationDto.getStatus().name()));
    }

    @Test
    void shouldNotUpdateVacationIsNotExist_thenException() throws Exception {
        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(17L);

        Employee employee = createEmployee();

        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, employee.getId(), Status.PENDING_APPROVAL);
        vacationDto.setId(999L);
        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(put("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(VACATION_DOES_NOT_EXIST));
    }

    @Test
    void shouldNotUpdateVacationDateStartNotExist_thenException() throws Exception {
        Vacation vacation = createVacation();

        LocalDate dateEnd = createVacationDate(17L);

        VacationDto vacationDto = createVacationDto(null, dateEnd, vacation.getEmployee().getId(), Status.PENDING_APPROVAL);
        vacationDto.setId(vacation.getId());

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(put("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_FROM_THIS_DATE));
    }

    @Test
    void shouldNotUpdateVacationDateEndNotExist_thenException() throws Exception {
        Vacation vacation = createVacation();

        LocalDate dateStart = createVacationDate(7L);

        VacationDto vacationDto = createVacationDto(dateStart, null, vacation.getEmployee().getId(), Status.PENDING_APPROVAL);
        vacationDto.setId(vacation.getId());

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(put("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_TO_THIS_DATE));
    }

    @Test
    void shouldNotUpdateVacationDateStartAndEndNotExist_thenException() throws Exception {
        Vacation vacation = createVacation();

        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(-8L);

        VacationDto vacationDto = createVacationDto(dateStart, dateEnd, vacation.getEmployee().getId(), Status.PENDING_APPROVAL);
        vacationDto.setId(vacation.getId());

        String json = objectMapper.writeValueAsString(vacationDto);
        mockMvc.perform(put("/api/vacations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CAN_NOT_TAKE_VACATION_ON_THIS_DATE));
    }

    private LocalDate createVacationDate(Long countOfDays) {
        return LocalDate.now().plusDays(countOfDays);
    }

    private String printFormatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setName(TEST_NAME);
        return departmentRepository.save(department);
    }

    private Position createPosition() {
        Position position = new Position();
        position.setName(TEST_NAME);
        return positionRepository.save(position);
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Test first name");
        employee.setLastName("Test last name");
        employee.setIsFired(false);
        employee.setDateOfBirth(LocalDate.of(1997, 04, 10));
        employee.setGender(Gender.WOMAN);
        employee.setDepartment(createDepartment());
        employee.setPosition(createPosition());
        employee.setBoss(null);
        return employeeRepository.save(employee);
    }

    private Vacation createVacation() {
        LocalDate dateStart = createVacationDate(7L);
        LocalDate dateEnd = createVacationDate(17L);

        Employee employee = createEmployee();

        Vacation vacation = new Vacation();
        vacation.setDateStart(dateStart);
        vacation.setDateEnd(dateEnd);
        vacation.setEmployee(employee);
        vacation.setStatus(null);
        return vacationRepository.save(vacation);
    }
}
