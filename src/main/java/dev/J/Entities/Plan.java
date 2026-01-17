package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @CreationTimestamp
    private LocalDate creationDate;

    @OneToMany(mappedBy = "plan",cascade = CascadeType.REMOVE)
    private Set<PlannedCourse> plannedCourses;

    @ManyToOne(fetch = FetchType.LAZY)
    private Consumer consumer;


    @OneToMany(
            mappedBy = "parentPlan",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    private List<PlanDegree> rootDegrees = new ArrayList<>();

    public Map<Course,PlannedCourse> plannedCoursesAsMap() {
        HashMap<Course,PlannedCourse> map = new HashMap<>();
        this.plannedCourses.forEach(plannedCourse -> map.put(plannedCourse.getCourse(),plannedCourse));
        return map;
    }

}
