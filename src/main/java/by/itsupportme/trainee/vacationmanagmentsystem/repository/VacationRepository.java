package by.itsupportme.trainee.vacationmanagmentsystem.repository;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Status;
import by.itsupportme.trainee.vacationmanagmentsystem.model.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long>, PagingAndSortingRepository<Vacation, Long> {

    @Query(value = "select v from Vacation v left join fetch v.employee")
    List<Vacation> findAllVacations();

    @Query(value = "select v from Vacation v left join fetch v.employee where (v.employee.id = :id or v.employee.boss.id = :id) and v.status = :status")
    List<Vacation> findAllMyVacationsAndBoss(@Param("id") Long id, @Param("status") Status status);
}
