package dev.J;

import dev.J.Entities.Course;
import dev.J.Entities.Requirement;
import dev.J.Entities.Type;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record EnumeratedRequirement(UUID requirementId, @Nullable String name, Type type, List<EnumeratedRequirement> childReqs, List<SchedulerEndpoint.CourseDTO> leafCourses) {

    public static EnumeratedRequirement of(Requirement requirement){
        if (requirement == null){
            return null;
        }
        List<EnumeratedRequirement> childR = requirement.getChildRequirements()
                .stream()
                .map(req -> EnumeratedRequirement.of(req))
                .toList();

        List<SchedulerEndpoint.CourseDTO> leafCouses = requirement.getLeafCourses().stream()
                .map(course -> new SchedulerEndpoint.CourseDTO(course.getId(),course.getCourseId(),course.getName(),course.getUnits(),course.getOwningCampus().getId()))
                .toList();

        return new EnumeratedRequirement(requirement.getId(),requirement.getName(),requirement.getType(),childR,leafCouses);
    }

}
