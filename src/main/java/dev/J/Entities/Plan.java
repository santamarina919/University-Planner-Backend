package dev.J.Entities;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    Consumer consumer;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<Degree> rootDegrees = new ArrayList<>();

}
