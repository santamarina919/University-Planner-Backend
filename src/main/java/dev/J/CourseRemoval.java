package dev.J;

import dev.J.Entities.*;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class CourseRemoval {

    private static List<Course> coursesToBeRemoved;

    private static String errorString;

    public static boolean removeCourse(Session session, UUID planId, UUID surrogateCourseId){
        Plan p = session.find(Plan.class,planId);
        Set<PlannedCourse> plannedCourses = p.getPlannedCourses();
        HashMap<Course,Integer> semesterCompleted = new HashMap<>();
        plannedCourses.forEach(plannedCourse -> semesterCompleted.put(plannedCourse.getCourse(),plannedCourse.getSemesterPlanned()));

        Course courseToBeRemoved = session.find(Course.class,surrogateCourseId);

        boolean alreadyPlanned = session.createQuery(
                "from PlannedCourse p " +
                        "where p.plan.id = :planId and " +
                        "p.course.id = :surrogateCourseId",
                        PlannedCourse.class
                )
                .setParameter(":planId",planId)
                .setParameter(":surrogateCourseId",surrogateCourseId)
                .getResultCount() == 1;
        if(alreadyPlanned){
            return false;
        }

        List<Prerequisite> affectedPrerequisites = session.createQuery(
                        "from Prerequisite p " +
                                "where :toBeRemovedId in p.childCourses",
                        Prerequisite.class
                )
                .setParameter(":toBeRemoved",courseToBeRemoved.getCourseId())
                .getResultList();


        if(affectedPrerequisites.isEmpty()){
            coursesToBeRemoved = List.of(courseToBeRemoved);
        }
        else{
            coursesToBeRemoved = removalAllAffectedCourses(plannedCourses,courseToBeRemoved,new ArrayList<>(),semesterCompleted,session);
        }
        return true;
    }

    private static List<Course> removalAllAffectedCourses(Set<PlannedCourse> plannedCourses,
                                                          Course originalCourse,
                                                          ArrayList<Course> coursesToBeRemoved,
                                                          HashMap<Course,Integer> semesterPlanned,
                                                          Session session) {
        Deque<Course> q = new LinkedList<>();
        q.add(originalCourse);

        while(!q.isEmpty()) {
            Course toBeRemoved = q.poll();
            coursesToBeRemoved.add(toBeRemoved);
            List<Prerequisite> affectedPrerequisites = session.createQuery(
                            "from Prerequisite p " +
                                    "where :toBeRemovedId in p.childCourses",
                            Prerequisite.class
                    )
                    .setParameter(":toBeRemoved",toBeRemoved.getCourseId())
                    .getResultList();

            List<Course> newlyRemoved = affectedPrerequisites
                    .stream()
                    .map(prereq -> traverseAffectedPrereq(prereq,semesterPlanned))
                    .filter(Objects::nonNull)
                    .toList();

            q.addAll(newlyRemoved);

        }


        return coursesToBeRemoved;
    }

    /**
     * Call this function on a prerequisite p when a previously completed course is removed. While traverse up tree and return
     * the course that is no longer complete as a result or null if it is still complete
     * @param p
     * @return
     */
    private static @Nullable Course traverseAffectedPrereq(Prerequisite p, HashMap<Course,Integer> semesterPlanned){
        Prerequisite prev = p;
        Prerequisite curr = p;
        while(curr != null){
            if(curr.getType() == Type.AND){
                prev = curr;
                curr = curr.getParentPrereq();
            }
            //Or node
            else {
                OptionalInt newSemesterCompleted = revalidateOrNode(p,semesterPlanned);
                if(newSemesterCompleted.isPresent()){
                    //new semester completed available via newSemesterCompleted.getAsInt()
                    return null;
                }
                else {
                    prev = curr;
                    curr = curr.getParentPrereq();
                }
            }
        }
        return prev.getParentCourse();
    }

    private static OptionalInt revalidateOrNode(Prerequisite rootNode,HashMap<Course,Integer> plannedCourses){
        OptionalInt earliestCompletedCourse = rootNode.getChildCourses().stream().filter(plannedCourses::containsKey)
                .mapToInt(plannedCourses::get)
                .min();

        OptionalInt earliestCompletedChildPrereq = rootNode.getChildPrereqs().stream()
                .map(prerequisite -> prerequisite.getType() == Type.AND ? revalidateAndNode(prerequisite,plannedCourses) : revalidateOrNode(prerequisite,plannedCourses))
                .filter(semester -> semester.isPresent())
                .mapToInt(OptionalInt::getAsInt)
                .min();
        if(earliestCompletedChildPrereq.isPresent() && earliestCompletedCourse.isPresent()){
            return earliestCompletedCourse.getAsInt() < earliestCompletedChildPrereq.getAsInt() ? earliestCompletedCourse : earliestCompletedChildPrereq;
        }
        else if(earliestCompletedChildPrereq.isPresent()){
            return earliestCompletedChildPrereq;
        }
        else {
            return earliestCompletedCourse;
        }

    }



    private static OptionalInt revalidateAndNode(Prerequisite rootNode, HashMap<Course,Integer> plannedCourses){
        boolean allCompleted = rootNode.getChildCourses().stream().allMatch(plannedCourses::containsKey);
        if(!allCompleted){
            return OptionalInt.empty();
        }
        OptionalInt largestSemeter = rootNode.getChildCourses().stream()
                .mapToInt(plannedCourses::get)
                .max();

        List<OptionalInt> semesterPrereqCompleted = rootNode.getChildPrereqs().stream()
                .map(prereq -> prereq.getType() == Type.OR ? revalidateOrNode(prereq,plannedCourses) : revalidateAndNode(prereq,plannedCourses))
                .toList();

        if(semesterPrereqCompleted.stream().anyMatch(OptionalInt::isEmpty)){
            return OptionalInt.empty();
        }

        OptionalInt largestSemesterPrereqCompl = semesterPrereqCompleted.stream().mapToInt(OptionalInt::getAsInt)
                .max();

        if(largestSemeter.isPresent() && largestSemesterPrereqCompl.isPresent()){
            return largestSemeter.getAsInt() > largestSemesterPrereqCompl.getAsInt() ? largestSemeter : largestSemesterPrereqCompl;
        }
        else if(largestSemeter.isPresent()) {
            return largestSemeter;
        }
        else{
            return largestSemesterPrereqCompl;
        }
    }
}
