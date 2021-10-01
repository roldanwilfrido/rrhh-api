package com.leantech.rrhh.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="position_id", nullable=false)
    private Position position;

    @ManyToOne
    @JoinColumn(name="person_id", nullable=false)
    private Person person;

    @Column
    private Integer salary;
}
