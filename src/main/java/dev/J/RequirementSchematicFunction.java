package dev.J;

//TODO: Implement a function where given a plan will go through each requirement for each plan.
//it will call the completion function for some root node and the child nodes of the root node will also have the funciton called.
//each node will record what its states is and store it in a hashmap that map will be returned and what the fron end will use

import dev.J.Entities.*;
import org.hibernate.Session;

import java.util.List;

public class RequirementSchematicFunction {

    public static List<DegreeSchematic> fetchRequirementSchematic(Session s, Plan p){
        List<PlanDegree> degrees = p.getRootDegrees();

        return degrees.stream().map(planDegree ->
                DegreeSchematic.fromDegree(planDegree.getChildDegree())
        ).toList();

}
}
