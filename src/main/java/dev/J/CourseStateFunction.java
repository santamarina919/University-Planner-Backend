package dev.J;

import dev.J.Entities.Course;
import dev.J.Entities.Plan;
import dev.J.Entities.PlannedCourse;
import dev.J.Entities.Prerequisite;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CourseStateFunction {
    public record CourseState(UUID id, String courseId, String name, int units, boolean isCompleted,
                              @Nullable Integer firstSemesterPlannable, @Nullable Integer semesterPlanned) {
    }

    public static List<CourseState> getStates(Session session, UUID planId) {
        Plan plan = session.find(Plan.class, planId);
        Map<Course, PlannedCourse> plannedCourses = plan.plannedCoursesAsMap();
        List<Course> allCoursesRelatedToPlan = plan.getRootDegrees()
                .stream()
                .map(planDegree -> planDegree.getChildDegree().getRootRequirement().relatedCourses())
                .flatMap(List::stream)
                .toList();

        return createCourseStates(session, plan, allCoursesRelatedToPlan,plannedCourses );
    }


    private static List<CourseState> createCourseStates(Session session, Plan p, List<Course> allCoursesRelatedToPlan, Map<Course, PlannedCourse> plannedCourses) {
        return allCoursesRelatedToPlan
                .stream()
                .map(course -> extractCourseState(session, p, course,plannedCourses))
                .toList();

    }

    private static CourseState extractCourseState(Session session, Plan p, Course currentCourse, Map<Course, PlannedCourse> plannedCourse) {
        PlannedCourse semesterPlannedRecord = plannedCourse.get(currentCourse);
        Integer semesterPlanned = null;
        if(semesterPlannedRecord != null){
            semesterPlanned = semesterPlannedRecord.getSemesterPlanned();
        }
        Prerequisite rootPrereq = currentCourse.getRootPrerequisite();
        Integer firstSemeterAvail = null;

        if(rootPrereq == null){
            firstSemeterAvail = 0;
        }
        else {
            firstSemeterAvail = currentCourse.getRootPrerequisite().findSemesterCompleted(p);
        }
        return new CourseState(
                currentCourse.getId(),
                currentCourse.getCourseId(),
                currentCourse.getName(),
                currentCourse.getUnits(),
                semesterPlanned != null  ,
                firstSemeterAvail == Prerequisite.NOT_COMPLETED ? null : firstSemeterAvail,
                semesterPlanned);
    }


}