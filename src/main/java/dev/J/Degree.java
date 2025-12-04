package dev.J;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
public class Degree {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    Campus owningCampus;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    Requirement rootRequirement;

}
