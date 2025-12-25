package dev.J;

import dev.J.Entities.Course;
import dev.J.Entities.Plan;
import dev.J.Entities.PlannedCourse;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CourseStateFunction {
    public record CourseState(UUID id, String courseId, String name, int units, boolean isCompleted, @Nullable Integer semesterCompleted){ }

    public static List<CourseState> getStates(Session session, UUID planId){
        Plan p = session.find(Plan.class,planId);
        Map<Course, PlannedCourse> plannedCourses = p.plannedCoursesAsMap();
        List<CourseState> allCoursesRelatedToPlan = p.getRootDegrees()
                .stream()
                .map(planDegree -> planDegree.getChildDegree().getRootRequirement().relatedCourses())
                .flatMap(List::stream)
                .map(course -> {
                    PlannedCourse plannedCourse = plannedCourses.get(course);
                    Integer semesterCompleted = plannedCourse == null ? null : plannedCourse.getSemesterPlanned();
                    return new CourseState(course.getId(),course.getCourseId(),course.getName(),course.getUnits(), plannedCourse != null,semesterCompleted);
                })
                .toList();
        return allCoursesRelatedToPlan;
    }
}
