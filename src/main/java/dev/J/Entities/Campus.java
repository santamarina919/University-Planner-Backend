package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.util.StringUtils;


@Data
@Entity
public class Campus {
    public Campus(){}
    //Basically the campus name
    @Id String id;

    @Embedded
    Address address;

    public String getName(){
        String[] words = id.split("(?=\\p{Upper})");
        return "California State University " + StringUtils.arrayToDelimitedString(words," ");
    }

}
