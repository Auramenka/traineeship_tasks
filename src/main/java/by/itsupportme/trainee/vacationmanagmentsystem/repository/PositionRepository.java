package by.itsupportme.trainee.vacationmanagmentsystem.repository;

import by.itsupportme.trainee.vacationmanagmentsystem.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}
