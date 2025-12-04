package dev.J;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.UUID;

@Entity
public class Consumer {
    @Id @GeneratedValue
    UUID id;

    @NaturalId
    String email;

    String password;

    String firstName;

    @OneToMany
    List<Plan> plans;

}
