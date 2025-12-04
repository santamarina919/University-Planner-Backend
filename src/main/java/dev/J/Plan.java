package dev.J;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    Consumer consumer;

}
