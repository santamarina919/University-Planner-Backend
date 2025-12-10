package dev.J.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Degree {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    Campus owningCampus;

    @OneToOne(optional = true,fetch = FetchType.LAZY)
    Requirement rootRequirement;

}
