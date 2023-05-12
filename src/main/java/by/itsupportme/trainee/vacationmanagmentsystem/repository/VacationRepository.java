package by.itsupportme.trainee.vacationmanagmentsystem.repository;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query(value = "select v from Vacation v left join fetch v.employee")
    List<Vacation> findAllVacations();
}
