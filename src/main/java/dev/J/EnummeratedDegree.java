package dev.J;

import dev.J.Entities.Campus;

import java.util.UUID;

public record EnummeratedDegree(UUID degreeId, String name, EnumeratedRequirement rootRequirement, Campus owningCampus) {
}
