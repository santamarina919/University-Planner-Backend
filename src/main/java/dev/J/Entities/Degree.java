package dev.J.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.ws.rs.GET;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Getter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"},ignoreUnknown = true)
public class Degree {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;


    @JsonIgnore
    @OneToMany(mappedBy = "childDegree")
    List<PlanDegree> plansWithDegree;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    Campus owningCampus;

    @OneToOne(optional = true,fetch = FetchType.LAZY)
    Requirement rootRequirement;

}
