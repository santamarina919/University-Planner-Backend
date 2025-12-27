package dev.J;

import dev.J.Entities.*;
import org.hibernate.Session;
import org.jspecify.annotations.Nullable;

import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CourseAddition {

    public static boolean addCourse(Session session, UUID planId, UUID surrogateCourseId,int semesterToBeAdded){
        Plan userPlan = session.find(Plan.class,planId);

        Course courseToBeAdded = session.find(Course.class,surrogateCourseId);

        int semesterPrereqCompleted = courseToBeAdded.getRootPrerequisite().findSemesterCompleted(userPlan);

        if(semesterPrereqCompleted == Prerequisite.NOT_COMPLETED){
            return false;
        }

        return semesterToBeAdded > semesterPrereqCompleted;
    }

}


