package dev.J;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

//FOR NOW ASSUME NO Untrackable requirements
@Entity
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Nullable
    String name;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    Degree owningDegree;

    @ManyToOne(optional = true,fetch = FetchType.LAZY)
    Requirement parentRequirement;

    @OneToMany(fetch = FetchType.LAZY)
    List<Requirement> childRequirements;

    @JoinTable(name = "requirementcourse")
    @ManyToMany(fetch = FetchType.LAZY)
    List<Course> leafCourses;

}
