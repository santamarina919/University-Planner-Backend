package dev.J;

import dev.J.Entities.*;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class CourseAddition {



    public static @Nullable List<CourseStateChange> addCourse(Session session, UUID planId, UUID surrogateCourseId,int semesterToBeAdded){
        Plan userPlan = session.find(Plan.class,planId);

        Course courseToBeAdded = session.find(Course.class,surrogateCourseId);

        boolean canBeAdded = verifyValidAddition(semesterToBeAdded,courseToBeAdded,userPlan);

        if(!canBeAdded){return null;}

        PlannedCourse newlyPlannedCourse = new PlannedCourse(userPlan,courseToBeAdded,semesterToBeAdded);
        userPlan.getPlannedCourses().add(newlyPlannedCourse);
        session.persist(newlyPlannedCourse);

        return findStateChanges(session,userPlan, courseToBeAdded,semesterToBeAdded);

    }

    private static List<CourseStateChange> findStateChanges(Session session, Plan plan, Course courseToBeAdded, int semesterAdded) {
        List<CourseStateChange> stateChanges = new ArrayList<>();
        stateChanges.add(new CourseStateChange(courseToBeAdded.getId(),CourseStateChange.DO_NOT_RESET_FLAG,semesterAdded));

        List<Prerequisite> affectedPrereqs = session.createQuery(
                "from Prerequisite p " +
                        "inner join p.childCourses c " +
                        "where c.id = :courseId",
                Prerequisite.class
                )
                .setParameter("courseId",courseToBeAdded.getId())
                .getResultList();


        findAvailableCourses(stateChanges,affectedPrereqs,plan);


        return stateChanges;
    }

    private static void findAvailableCourses(List<CourseStateChange> state,List<Prerequisite> affectedPrereqs, Plan plan) {
        Queue<Prerequisite> q = new LinkedList<>(affectedPrereqs);
        while (!q.isEmpty()){
            Prerequisite currentP = q.poll();
            while(currentP.getParentPrereq() != null){
                currentP = currentP.getParentPrereq();
            }
            Integer semesterCompleted = currentP.findSemesterCompleted(plan);
            if(semesterCompleted != Prerequisite.NOT_COMPLETED){
                Course parentCourse = currentP.getParentCourse();
                assert parentCourse != null;
                if(plan.plannedCoursesAsMap().get(parentCourse) == null) {
                    state.add(new CourseStateChange(parentCourse.getId(), semesterCompleted + 1, null));
                }
            }
        }
    }

    /**
     * Verify that the course can be added to the plan P at semester s
     * @param semesterToBeAdded
     * @param courseToBeAdded
     * @param userPlan
     * @return
     */
    private static boolean verifyValidAddition(int semesterToBeAdded, Course courseToBeAdded, Plan userPlan) {
        Prerequisite rootPrerequisite = courseToBeAdded.getRootPrerequisite();
        if(rootPrerequisite == null){
            return true;
        }
        int semesterPrereqCompleted = rootPrerequisite.findSemesterCompleted(userPlan);

        if(semesterPrereqCompleted == Prerequisite.NOT_COMPLETED){
            return false;
        }

        return semesterToBeAdded > semesterPrereqCompleted;
    }

}


