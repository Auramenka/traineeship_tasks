package by.itsupportme.trainee.vacationmanagmentsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorMessage {
    private int statusCode;
    private String message;
    private String description;
}
