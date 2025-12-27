package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
public class Prerequisite {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Prerequisite> childPrereqs;

    @ManyToOne
    private Prerequisite parentPrereq;

    @Nullable
    @OneToOne(mappedBy = "rootPrerequisite")
    private Course parentCourse;

    @JoinTable(name = "prerequisitecourse")
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Course> childCourses;

    public static final int NOT_COMPLETED = -1;

    public int findSemesterCompleted(Plan p){
        return findSemesterCompleted(p,p.plannedCoursesAsMap());
    }

    private int findSemesterCompleted(Plan p, Map<Course, PlannedCourse> completedCourses){
        int leafNodeCompleted = leafNodesCompleted(p,completedCourses);
        int childNodesCompleted = childNodesCompleted(p,completedCourses);
        if(this.type == Type.AND){
            if(leafNodeCompleted == NOT_COMPLETED || childNodesCompleted == NOT_COMPLETED) return NOT_COMPLETED;
            else return Math.max(leafNodeCompleted,childNodesCompleted);
        }
        else{
            if(leafNodeCompleted == NOT_COMPLETED && childNodesCompleted == NOT_COMPLETED)return NOT_COMPLETED;
            else return Math.min(leafNodeCompleted,childNodesCompleted);
        }
    }

    private int leafNodesCompleted(Plan p,Map<Course,PlannedCourse> completedCourses){
        if(type == Type.AND){
            return andLeafNodeCheck(p,completedCourses);
        }
        else {
            return orLeafNodeCheck(p,completedCourses);
        }
    }

    private int andLeafNodeCheck(Plan p, Map<Course,PlannedCourse> completedCourses){
        boolean allCompleted = this.childCourses
                .stream()
                .allMatch(completedCourses::containsKey);
        if(!allCompleted) return NOT_COMPLETED;

        int semesterCompleted = this.childCourses
                .stream()
                .mapToInt(course -> completedCourses.get(course).getSemesterPlanned())
                .max()
                .getAsInt();
        return semesterCompleted;
    }

    public int orLeafNodeCheck(Plan p, Map<Course, PlannedCourse> completedCourses){
        boolean noneCompleted = this.childCourses
                .stream()
                .allMatch(course -> !completedCourses.containsKey(course));
        if(noneCompleted) return NOT_COMPLETED;

        int semesterCompleted = this.childCourses
                .stream()
                .filter(completedCourses::containsKey)
                .mapToInt(course -> completedCourses.get(course).getSemesterPlanned())
                .min()
                .getAsInt();
        return semesterCompleted;
    }

    private int childNodesCompleted(Plan p,Map<Course,PlannedCourse> completedCourses){
        if(type == Type.AND){
            return andChildNodeCheck(p,completedCourses);
        }
        else {
            return orChildNodeCheck(p,completedCourses);
        }
    }

    private int andChildNodeCheck(Plan p, Map<Course,PlannedCourse> completedCourses){
        boolean allCompleted = true;
        int largestCompleted = Integer.MIN_VALUE;
        for(Prerequisite prereq : this.childPrereqs){
            int semesterCompleted = prereq.findSemesterCompleted(p,completedCourses);
            if(semesterCompleted == NOT_COMPLETED) {
                allCompleted = false;
                break;
            }

            largestCompleted = Math.max(largestCompleted,semesterCompleted);
        }
        if(!allCompleted) return NOT_COMPLETED;
        return largestCompleted;
    }

    private int orChildNodeCheck(Plan p, Map<Course,PlannedCourse> completedCourses){
        boolean noneCompleted = true;
        int smallestSemester = Integer.MAX_VALUE;
        for(Prerequisite prereq : this.childPrereqs){
            int semesterCompleted = prereq.findSemesterCompleted(p,completedCourses);
            if(semesterCompleted == NOT_COMPLETED) {
                continue;
            }
            noneCompleted = false;
            smallestSemester = Math.min(smallestSemester,semesterCompleted);
        }
        if(noneCompleted) return NOT_COMPLETED;
        return smallestSemester;
    }

}
