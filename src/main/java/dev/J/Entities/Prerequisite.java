package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
public class Prerequisite {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Prerequisite> childPrereqs;

    @ManyToOne
    private Prerequisite parentPrereq;

    @Nullable
    @OneToOne(mappedBy = "rootPrerequisite")
    private Course parentCourse;

    @JoinTable(name = "prerequisitecourse")
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Course> childCourses;
}
