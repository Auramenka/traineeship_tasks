package by.itsupportme.trainee.vacationmanagmentsystem.repository;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
