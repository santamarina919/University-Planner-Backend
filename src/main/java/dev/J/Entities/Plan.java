package dev.J.Entities;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
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

    @CreationTimestamp
    private LocalDate creationDate;

    @OneToMany(mappedBy = "plan")
    private Set<PlannedCourse> plannedCourses;

    @ManyToOne(fetch = FetchType.LAZY)
    private Consumer consumer;


    @OneToMany(
            mappedBy = "parentPlan",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    private List<PlanDegrees> rootDegrees = new ArrayList<>();

}
