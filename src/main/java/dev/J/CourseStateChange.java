package dev.J;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Class indicates that a course state has changes and notifies the front end of this change.
 * If one of the nullable fields are null it indicates that there is no change
 *
 * @param id
 * @param firstSemesterPlannable
 * @param semesterPlanned
 */
public record CourseStateChange(UUID id, @Nullable Integer firstSemesterPlannable, @Nullable Integer semesterPlanned) {

    public static int RESET_FLAG = -1;

    public static @Nullable Integer DO_NOT_RESET_FLAG = null;

    public static int RESET_TO_INITIALLY_AVAILABLE = -2;

    public static CourseStateChange resetState(UUID id){
        return new CourseStateChange(id,RESET_FLAG,RESET_FLAG);
    }

    public static CourseStateChange resetToInitiallyAvailable(UUID id){
        return new CourseStateChange(id,RESET_TO_INITIALLY_AVAILABLE,RESET_FLAG);
    }



}
