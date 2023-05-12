package by.itsupportme.trainee.vacationmanagmentsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="vacations")
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "date_start")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateStart;

    @Column(name = "date_end")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "status_of_vacation")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
