package dev.J.Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlannedCourse {

    @Id
    @ManyToOne
    private Plan plan;

    @Id
    @ManyToOne
    private Course course;

    @NonNull
    @Column(nullable = false)
    private Integer semesterPlanned;

    @Override
    public int hashCode(){
        return Objects.hash(plan.getId(),course.getId(),semesterPlanned);
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof PlannedCourse)){
            return false;
        }
        else{
            return ((PlannedCourse) obj).plan.getId().equals(this.plan.getId())
                    && ((PlannedCourse) obj).course.getId().equals(this.course.getId())
                    && Objects.equals(((PlannedCourse) obj).semesterPlanned, semesterPlanned);
        }
    }

}
