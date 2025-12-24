package dev.J.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class PlanDegrees {

    @Id
    @ManyToOne
    Plan parentPlan;

    @Id
    @ManyToOne
    Degree childDegree;


}
