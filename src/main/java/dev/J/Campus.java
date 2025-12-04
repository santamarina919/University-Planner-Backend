package dev.J;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Entity
public class Campus {
    public Campus(){}
    //Basically the campus name
    @Id String id;

    @Embedded
    Address address;

}
