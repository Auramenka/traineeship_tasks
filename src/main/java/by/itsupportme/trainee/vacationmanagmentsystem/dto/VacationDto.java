package by.itsupportme.trainee.vacationmanagmentsystem.dto;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationDto {

    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateStart;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateEnd;
    private Long employeeId;
    private Status status;
}
