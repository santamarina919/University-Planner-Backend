package dev.J.Entities;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "plan")
    private Set<PlannedCourse> plannedCourses;

    @ManyToOne(fetch = FetchType.LAZY)
    private Consumer consumer;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Degree> rootDegrees = new ArrayList<>();

}
