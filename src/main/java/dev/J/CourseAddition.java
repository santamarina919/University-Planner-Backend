package dev.J;

import dev.J.Entities.*;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CourseAddition {

    @Nullable
    private static String errorReason = null;

    @Nullable
    private static UUID lastPlanIdUsed = null;
    @Nullable
    private static UUID lastCourseIdUsed = null;
    @Nullable
    private static Integer semester = null;

    public record LastCallParameters(UUID planId, UUID courseId, int semester){}

    public static boolean addCourse(Session session, UUID planId, UUID surrogateCourseId,int semester){
        Plan userPlan = session.find(Plan.class,planId);
        Set<PlannedCourse> plannedCourses = userPlan.getPlannedCourses();

        Course courseToBeAdded = session.find(Course.class,surrogateCourseId);
        Map<Course,Integer> semesterPlanned = new HashMap<>();
        plannedCourses.forEach(plannedCourse -> {
            semesterPlanned.put(plannedCourse.getCourse(),plannedCourse.getSemesterPlanned());
        });

        PlannedCourse addedCourse = new PlannedCourse();
        addedCourse.setCourse(courseToBeAdded);
        addedCourse.setPlan(userPlan);
        addedCourse.setSemesterPlanned(semester);

        if(plannedCourses.contains(addedCourse)){
            errorReason = "Course was already added to plan";
            return false;
        }

        boolean canBeAdded;
        if(courseToBeAdded.getRootPrerequisite() != null) {
            int semesterRootPrereqCompleted = validateNode(courseToBeAdded.getRootPrerequisite(), semesterPlanned, semester);
            canBeAdded = semesterRootPrereqCompleted != INVALID_COMPLETION && semesterRootPrereqCompleted < semester ;
        } else {
            canBeAdded = true;
        }

        if(canBeAdded) {
            session.persist(addedCourse);
            plannedCourses.add(addedCourse);
        }
        return canBeAdded;
    }

    private static final int INVALID_COMPLETION = -1;


    /**
     *
     * @param rootPrerequisite
     * @param semesterPlannedMap
     * @param semesterToInsertTo
     * @return the semester this node was satisfied
     */
    private static int validateNode(Prerequisite rootPrerequisite, Map<Course, Integer> semesterPlannedMap, int semesterToInsertTo) {
        int semesterCompleted;
        if(rootPrerequisite.getType() == Type.OR){
            semesterCompleted = validateOrNode(rootPrerequisite,semesterPlannedMap,semesterToInsertTo);
        }
        else if(rootPrerequisite.getType() == Type.AND){
            semesterCompleted = validateAndNode(rootPrerequisite,semesterPlannedMap,semesterToInsertTo);
        }
        else{
            throw new IllegalStateException("Encountered Prerequisite type that is invalid");
        }
        return semesterCompleted;
    }

    public static int validateOrNode(Prerequisite rootPrerequisite, Map<Course, Integer> semesterPlannedMap, int semesterToInsertTo) {
        assert rootPrerequisite.getType() == Type.OR;

        boolean noneCompletedFlag = true;
        int smallestSemesterComppleted = Integer.MAX_VALUE;
        for(Course c : rootPrerequisite.getChildCourses()){
            Integer semesterCompleted = semesterPlannedMap.get(c);
            if(semesterCompleted != null && semesterCompleted < semesterToInsertTo){
                smallestSemesterComppleted = Math.min(smallestSemesterComppleted, semesterCompleted);
                noneCompletedFlag = false;
            }
            else if(semesterCompleted == null){

            }
            //semester completed >= semester to insert to
            else {

            }
        }

        for(Prerequisite p : rootPrerequisite.getChildPrereqs()){
            Integer semesterCompleted;
            if(p.getType() == Type.AND){
                semesterCompleted = validateAndNode(rootPrerequisite,semesterPlannedMap,semesterToInsertTo);
            }
            else if(p.getType() == Type.OR){
                semesterCompleted = validateOrNode(rootPrerequisite,semesterPlannedMap,semesterToInsertTo);
            }
            else {
                throw new IllegalArgumentException("Encountered undeclared prerequisite type");
            }

            if(semesterCompleted != INVALID_COMPLETION && semesterCompleted < semesterToInsertTo) {
                smallestSemesterComppleted = Math.min(smallestSemesterComppleted, semesterCompleted);
                noneCompletedFlag = false;
            }
            else if(semesterCompleted >= semesterToInsertTo){

            }
            //semestercompleted == invalid completion
            else {

            }

        }
        if(noneCompletedFlag){
            return INVALID_COMPLETION;
        }
        else {
            return smallestSemesterComppleted;
        }
    }

    private static int validateAndNode(Prerequisite rootPrerequisite, Map<Course, Integer> semesterPlannedMap, int semesterToInsertTo) {
        assert rootPrerequisite.getType() == Type.AND;

        boolean oneUncompleted = false;
        int largestCompleted = Integer.MIN_VALUE;
        for (Course c : rootPrerequisite.getChildCourses()) {
            Integer semesterCompleted = semesterPlannedMap.get(c);
            if (semesterCompleted == null) {
                oneUncompleted = true;
            }
            else if(semesterCompleted >= semesterToInsertTo){
                oneUncompleted = true;
            }
            else {
                largestCompleted = Math.max(largestCompleted, semesterCompleted);
            }
        }

        for (Prerequisite p : rootPrerequisite.getChildPrereqs()) {
            int semesterCompleted;
            if (p.getType() == Type.AND) {
                semesterCompleted = validateAndNode(rootPrerequisite, semesterPlannedMap, semesterToInsertTo);
            } else if (p.getType() == Type.OR) {
                semesterCompleted = validateOrNode(rootPrerequisite, semesterPlannedMap, semesterToInsertTo);
            } else {
                throw new IllegalStateException("Unexpected type encountered");
            }
            if (semesterCompleted != INVALID_COMPLETION && semesterCompleted < semesterToInsertTo) {
                largestCompleted = Math.max(largestCompleted, semesterCompleted);
            } else if (semesterCompleted == INVALID_COMPLETION) {
                oneUncompleted = true;
            }
            //semester completed >= semester to insert to
            else {
                oneUncompleted = true;
            }
        }

        if (oneUncompleted) {
            return INVALID_COMPLETION;
        } else {
            return largestCompleted;
        }
    }

    public String errorDescription(){
        if(errorReason == null){
            throw new IllegalStateException("Cannot access error if not error is presesnt");
        }
        return errorReason;
    }

    public LastCallParameters lastCallParameters(){
        if(lastPlanIdUsed == null || lastCourseIdUsed == null || semester == null){
            throw new IllegalStateException("Cannot access last call parameters if no error occured");
        }
        return new LastCallParameters(lastPlanIdUsed,lastCourseIdUsed,semester);
    }


}


