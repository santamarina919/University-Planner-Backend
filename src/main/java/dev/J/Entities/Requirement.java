package dev.J.Entities;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.io.NotSerializableException;
import java.util.*;

//FOR NOW ASSUME NO Untrackable requirements
@Entity
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Nullable
    String name;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    Degree owningDegree;

    @ManyToOne(optional = true,fetch = FetchType.LAZY)
    Requirement parentRequirement;

    @OneToMany(fetch = FetchType.LAZY)
    List<Requirement> childRequirements;

    @JoinTable(name = "requirementcourse")
    @ManyToMany(fetch = FetchType.LAZY)
    List<Course> leafCourses;

    public static final int NOT_SATISFIED = -1;

    //returns courses that are releated to this requirment.
    public List<Course> relatedCourses(){
        List<Course> allCourses = new ArrayList<>();

        Deque<Requirement> q = new LinkedList<>();
        q.add(this);
        while (!q.isEmpty()){
            Requirement r = q.poll();

            allCourses.addAll(r.leafCourses);

            q.addAll(r.childRequirements);
        }
        return Collections.unmodifiableList(allCourses);
    }



    public int requirementSatisfied(Plan p){
        int leafCourseCompletion = leafCoursesSatisfied(p);
        int reqCompleted = reqSatisfied(p);
        if(this.type == Type.AND){
            if(leafCourseCompletion == NOT_SATISFIED || reqCompleted == NOT_SATISFIED) return NOT_SATISFIED;
            return Math.max(leafCourseCompletion,reqCompleted);
        }
        else {
            if(leafCourseCompletion == NOT_SATISFIED && reqCompleted == NOT_SATISFIED) return NOT_SATISFIED;
            return Math.min(leafCourseCompletion,reqCompleted);
        }
    }

    private int reqSatisfied(Plan p){
        return this.type == Type.AND ? andNodeRequirementSatisfied(p) :
                orNodeRequirementSatisfied(p);
    }

    private int orNodeRequirementSatisfied(Plan p){
        boolean allUncompleted = true;
        int smallest = Integer.MAX_VALUE;
        for(Requirement childReq : this.childRequirements){
            int semCompleted = childReq.requirementSatisfied(p);
            if(semCompleted == NOT_SATISFIED) continue;
            allUncompleted = false;
            smallest = Math.min(semCompleted,smallest);
        }
        if(allUncompleted) return NOT_SATISFIED;
        else return smallest;
    }

    private int andNodeRequirementSatisfied(Plan p){
        int largest = Integer.MIN_VALUE;
        for(Requirement childReq : this.childRequirements){
            int semCompleted = childReq.requirementSatisfied(p);
            if(semCompleted == NOT_SATISFIED) return NOT_SATISFIED;
            largest = Math.max(largest,semCompleted);
        }
        return largest;
    }

    public int leafCoursesSatisfied(Plan p){
        return this.type == Type.AND ? andNodeLeafCoursesSatisfied(p) :
                OrNodeLeafCourseSatisfied(p);
    }

    private int OrNodeLeafCourseSatisfied(Plan p){
        Map<Course,PlannedCourse> plannedCourses = p.plannedCoursesAsMap();

        boolean allUncompleted  = this.leafCourses.stream().allMatch(course -> !plannedCourses.containsKey(course));
        if(allUncompleted) return NOT_SATISFIED;

        int smallestCourseCompleted = this.leafCourses
                .stream()
                .filter(course -> plannedCourses.get(course) != null)
                .mapToInt(course -> plannedCourses.get(course).getSemesterPlanned())
                .min()
                .getAsInt();
        return smallestCourseCompleted;
    }

    /**
     * Returns the semester a plan satified this requirement. Since it is a node of type AND that semester will be the largest semester
     * out of all the completed courses
     * @param p
     * @return
     */
    private int andNodeLeafCoursesSatisfied(Plan p){
        Map<Course,PlannedCourse> plannedCourses = p.plannedCoursesAsMap();

        boolean containsUncompletedCourse = this.leafCourses.stream().anyMatch(course -> !plannedCourses.containsKey(course));
        if(containsUncompletedCourse) return NOT_SATISFIED;

        int largestSemesterCourseCompleted = this.leafCourses.stream()
                .mapToInt(course -> plannedCourses.get(course).getSemesterPlanned())
                .max()
                .getAsInt();
        return largestSemesterCourseCompleted;
    }





}
