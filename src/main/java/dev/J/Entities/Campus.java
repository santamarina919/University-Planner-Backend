package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Campus {
    public Campus(){}
    //Basically the campus name
    @Id String id;

    @Embedded
    Address address;

}
