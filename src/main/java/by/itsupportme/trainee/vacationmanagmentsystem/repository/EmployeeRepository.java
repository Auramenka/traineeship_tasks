package by.itsupportme.trainee.vacationmanagmentsystem.repository;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = "select e from Employee e " +
            "left join fetch e.department left join fetch e.position")
    List<Employee> findAllEmployees();

    @Query(value = "select e from Employee e where e.id in :ids")
    List<Employee> findByEmployeeIds(@Param("ids") List<Long> ids);
}
