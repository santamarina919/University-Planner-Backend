package dev.J.Entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Prerequisite {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @OneToMany(fetch = FetchType.LAZY)
    List<Prerequisite> childPrereqs;

    @ManyToOne
    Prerequisite parentPrereq;

    @JoinTable(name = "prerequisitecourse")
    @ManyToMany(fetch = FetchType.LAZY)
    List<Course> childCourses;
}
