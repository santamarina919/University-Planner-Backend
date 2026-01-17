package dev.J;

import dev.J.Entities.Campus;
import dev.J.Entities.PlanDegree;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PlanDetails(UUID id, String name, LocalDate creationDate, Campus campus) {
}
