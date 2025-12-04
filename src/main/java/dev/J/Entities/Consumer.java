package dev.J.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.ws.rs.GET;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Consumer {
    @Id @GeneratedValue
    private UUID id;

    @NaturalId
    private String email;

    private String password;

    private String firstName;

    @OneToMany
    private List<Plan> plans;

}
