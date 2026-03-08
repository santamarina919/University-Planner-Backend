package dev.J;

import dev.J.Entities.Course;
import dev.J.Entities.Requirement;
import dev.J.Entities.Type;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record RequirementSchematic(UUID id, @Nullable String name, Type type, List<RequirementSchematic> childRequirements,
                                   List<UUID> leafCourses) {
    public static RequirementSchematic fromRequirement(Requirement r) {
        return new RequirementSchematic(
                r.getId(),
                r.getName(),
                r.getType(),
                r.getChildRequirements().stream().map(req -> RequirementSchematic.fromRequirement(r)).toList(),
                r.getLeafCourses().stream().map(Course::getId).toList()
        );
    }
}
