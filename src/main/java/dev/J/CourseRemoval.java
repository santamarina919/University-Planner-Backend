package dev.J;

import dev.J.Entities.*;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class CourseRemoval {

    public static List<CourseStateChange> removeCourse(Session session, UUID planId, UUID surrogateCourseId){

        Plan p = session.find(Plan.class,planId);
        Map<Course, PlannedCourse> completedCourses = p.plannedCoursesAsMap();

        Course courseToBeRemoved = session.find(Course.class,surrogateCourseId);

        if(!completedCourses.containsKey(courseToBeRemoved)){
            //cannot remove a course that is not planned
            return Collections.emptyList();
        }

        PlannedCourse enrollmentRecord = completedCourses.get(courseToBeRemoved);
        session.remove(enrollmentRecord);
        p.getPlannedCourses().remove(enrollmentRecord);

        return removalAllAffectedCourses(completedCourses,courseToBeRemoved,session);
    }

    private static List<CourseStateChange> removalAllAffectedCourses(Map<Course, PlannedCourse>  plannedCourses,
                                                          Course originalCourse,
                                                          Session session) {
        List<Course> removedCoursesList = new ArrayList<>();
        Deque<Course> q = new LinkedList<>();
        q.add(originalCourse);

        while(!q.isEmpty()) {
            Course currentCourse = q.poll();
            removedCoursesList.add(currentCourse);
            List<Prerequisite> affectedPrerequisites = findAffectedPrerequisites(session, currentCourse);

            List<Course> nextToBeRemoved = affectedPrerequisites
                    .stream()
                    .map(prereq -> plannedCourses.get(updatePossiblyPlannableCourses(prereq,plannedCourses,session,removedCoursesList)))
                    .filter(Objects::nonNull)
                    .map(PlannedCourse::getCourse)
                    .toList();

            q.addAll(nextToBeRemoved);
        }

        List<CourseStateChange> stateChanges = new ArrayList<>();
        stateChanges.add(new CourseStateChange(removedCoursesList.get(0).getId(),CourseStateChange.DO_NOT_RESET_FLAG,CourseStateChange.RESET_FLAG));
        for(Course removed : removedCoursesList.subList(1,removedCoursesList.size())){
            if(removed.getRootPrerequisite() == null)
                stateChanges.add(CourseStateChange.resetToInitiallyAvailable(removed.getId()));
            else
                stateChanges.add(CourseStateChange.resetState(removed.getId()));
        }
        return stateChanges;
    }

    private static List<Prerequisite> findAffectedPrerequisites(Session session, Course currentCourse) {
        var pList =  session.createQuery(
                  "from Prerequisite p " +
                            "inner join p.childCourses courses " +
                            "where courses.id = :toBeRemoved",
                        Prerequisite.class
                )
                .setParameter("toBeRemoved", currentCourse.getId())
                .getResultList();
        return pList;
    }


    private static Course updatePossiblyPlannableCourses(Prerequisite startNode, Map<Course, PlannedCourse> plannedCourses, Session session, List<Course> notAvailableCourseList){
        Course c = startNode.findParentCourse();

        if(plannedCourses.containsKey(c)){
            return c;
        }
        notAvailableCourseList.add(c);
        findAffectedPrerequisites(session,c)
                .stream()
                .map(Prerequisite::findParentCourse)
                .forEach(notAvailableCourseList::add);

        return c;
    }



}
