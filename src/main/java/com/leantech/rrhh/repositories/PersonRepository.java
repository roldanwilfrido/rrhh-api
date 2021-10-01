package com.leantech.rrhh.repositories;

import com.leantech.rrhh.models.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT p FROM Person p WHERE p.name = ?1 AND p.lastName = ?2")
    Optional<Person> getByNameAndLastName(String name, String lastName);

}
