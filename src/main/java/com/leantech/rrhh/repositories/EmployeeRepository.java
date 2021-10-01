package com.leantech.rrhh.repositories;

import com.leantech.rrhh.models.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT p FROM Employee p WHERE p.position.id = ?1 AND p.person.name = ?2 ORDER BY p.salary DESC")
    List<Employee> getByPositionAndPersonName(Integer position, String name);

    @Query("SELECT p FROM Employee p WHERE p.position.id = ?1 ORDER BY p.salary DESC")
    List<Employee> getByPosition(Integer position);

    @Query("SELECT p FROM Employee p WHERE p.position.id = ?1 AND p.person.id = ?2 ORDER BY p.salary DESC")
    Optional<Employee> getByPositionAndPerson(Integer position, Integer person);
}
