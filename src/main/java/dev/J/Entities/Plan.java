package dev.J.Entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    Consumer consumer;

    @ManyToMany
    List<Degree> rootDegrees;

}
