package com.leantech.rrhh.repositories;

import com.leantech.rrhh.models.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Integer> {
    @Query("SELECT p FROM Position p WHERE p.name = ?1")
    Optional<Position> getByName(String name);
}
