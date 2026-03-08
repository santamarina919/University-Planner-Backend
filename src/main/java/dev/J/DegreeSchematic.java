package dev.J;

import dev.J.Entities.Degree;

import java.util.UUID;

public record DegreeSchematic(UUID id, String name, RequirementSchematic requirement) {

    public static DegreeSchematic fromDegree(Degree d) {
        return new DegreeSchematic(d.getId(), d.getName(), RequirementSchematic.fromRequirement(d.getRootRequirement()));
    }
}
