package by.itsupportme.trainee.vacationmanagmentsystem.dto;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    private Gender gender;
    private Boolean isFired;
    private DepartmentDto departmentDto;
    private PositionDto positionDto;
    private List<VacationDto> vacationDto;
    private Long bossId;
}
