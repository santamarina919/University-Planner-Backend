package dev.J.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PlanDegrees {

    @Id
    @ManyToOne
    Plan parentPlan;

    @Id
    @ManyToOne
    Degree childDegree;


}
