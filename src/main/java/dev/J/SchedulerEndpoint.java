package dev.J;

import dev.J.Entities.*;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class SchedulerEndpoint {

    private final SessionFactory sessionFactory;

    //Hold ids of supported universities in a hardcoded list. However, in the future all CSUs will be supported.
    private static final List<String> supportedUniversities = List.of("DominguezHills","Bakersfield");

    @GetMapping("/scheduler/universities/supported")
    public List<Campus> supportedUniversities() {
        return supportedUniversities
                .stream()
                .map(id ->
                        sessionFactory.callInTransaction(
                                session -> session.find(Campus.class,id)
                        )
                )
                .toList();
    }

    @GetMapping("/scheduler/universities/degrees")
    public List<Degree> allDegrees() {
        return supportedUniversities.stream()
                .map(this::fetchDegrees)
                .flatMap(List::stream)
                .toList();
    }

    @GetMapping("/scheduler/universities/degreesOf")
    public List<Degree> degreesFrom(@RequestParam("campusId") String campusId){
        return fetchDegrees(campusId);
    }


    public List<Degree> fetchDegrees(String campusId) {
       return this.sessionFactory.fromTransaction(session ->
                   session
                           .createQuery(
                                "SELECT d " +
                                "FROM Degree d " +
                                "WHERE d.owningCampus.id = :campusId ",
                                Degree.class
                           )
                           .setParameter("campusId", campusId)
                           .stream().map(degree -> {
                               degree = session.merge(degree);
                               Hibernate.initialize(degree.getOwningCampus());
                               return degree;
                           })
                           .toList());
    }

    public record CourseDTO(UUID id, String courseId, String name, int units, String campusId){}

    @GetMapping("/scheduler/universities/allcourses")
    public List<CourseDTO> courses(){
        return sessionFactory
                .fromTransaction(session ->
                        session
                                .createQuery(
                                        "select new dev.J.SchedulerEndpoint$CourseDTO(c.id,c.courseId,c.name,c.units,c.owningCampus.id) " +
                                                "FROM Course c",
                                        CourseDTO.class)
                                .getResultList());
    }


    public record CreatePlanBody(String name, List<UUID> degreeIds){}

    //todo redirect user to page of the newly created plan
    @PostMapping("/scheduler/plans/create")
    public UUID createPlan(Authentication auth, @RequestBody CreatePlanBody body){
        UUID planId = sessionFactory.fromTransaction(session -> {

            UserDetails details = (UserDetails) auth.getPrincipal();
            assert details != null;
            Consumer owner = session.bySimpleNaturalId(Consumer.class).getReference(details.getUsername());



            Plan p = new Plan();
            p.setConsumer(owner);
            p.setName(body.name);
            List<PlanDegree> degrees = body.degreeIds.stream()
                    .map(id -> session.find(Degree.class,id))
                    .map(degree -> new PlanDegree(p,degree))
                    .toList();
            p.getRootDegrees().addAll(degrees);
            session.persist(p);
            return p.getId();
        });
        return planId;
    }

    @GetMapping("/scheduler/plans/myplans")
    public List<PlanDetails> allPlansFromUser(Authentication auth){
        Consumer user = (Consumer) auth.getPrincipal();
        assert user != null;
        return this.sessionFactory
                .fromTransaction(session ->
                        session.createQuery(
                                "select new dev.J.PlanDetails(p.id,p.name,p.creationDate,c) " +
                                        "from Plan p " +
                                        "inner join PlanDegree pd on p.id = pd.parentPlan.id " +
                                        "inner join Degree d on pd.childDegree.id = d.id " +
                                        "inner join Campus c on d.owningCampus.id = c.id " +
                                        "where p.consumer.id = :owner",
                                        PlanDetails.class)
                                .setParameter("owner",user.getId())
                                .getResultList()
                );
    }

    public record DegreeDTO(UUID id, String name){}

    @GetMapping("/scheduler/plans/childdegrees")
    public List<DegreeDTO> degreesOfPlan(Authentication auth, @RequestParam("planId") UUID planId){
        Consumer c = (Consumer)auth.getPrincipal();
        assert c != null;
        return this.sessionFactory.fromSession(session ->
                session.createQuery(
                        "select new DegreeDTO(d.id,d.name) " +
                                "from Plan p " +
                                "inner join PlanDegree pd on p.id = pd.parentPlan.id " +
                                "inner join Degree d on pd.childDegree.id = d.id " +
                                "where pd.id.parentPlan.id = :planId",
                                DegreeDTO.class
                        )
                        .setParameter("planId",planId)
                        .getResultList()
        );

    }


    //todo init plan endpoints
    //todo courses states

    public record StateChanges(@Nullable List<CourseStateChange> courseStateChanges){}


    //mutates state of plan
    @PostMapping("/scheduler/plans/{planId}/add")
    public StateChanges addCourse(
            @PathVariable("planId") UUID planId,
            @RequestParam("courseId") UUID courseId,
            @RequestParam("semester") int semester,
            @Nullable @RequestBody List<CourseStateChange> withState
    ){
        if(withState != null){
            applyStateToPlan(planId,withState);
        }
        List<CourseStateChange> courseStateChanges = sessionFactory.fromTransaction(session -> CourseAddition.addCourse(session,planId,courseId,semester));
        return new StateChanges(courseStateChanges);
    }
    //mutates state of plan
    @PostMapping("/scheduler/plans/{planId}/remove")
    public StateChanges removeCourse(
            @PathVariable("planId") UUID planId,
            @RequestParam("courseId") UUID courseId,
            @Nullable @RequestBody List<CourseStateChange> withState
    ){
        if(withState != null){
            applyStateToPlan(planId,withState);
        }
        var courseStateChanges = this.sessionFactory.fromTransaction(session -> CourseRemoval.removeCourse(session,planId,courseId));
        return new StateChanges(courseStateChanges);
    }


    @GetMapping("/scheduler/plans/course-states")
    public List<CourseStateFunction.CourseState> courseStatesOf(@RequestParam("planId") UUID planId, Authentication auth){
        Consumer c = (Consumer) auth.getPrincipal();
        assert c != null : "Could not case Authentication obj to Consumer class";
        return this.sessionFactory.fromTransaction(session -> CourseStateFunction.getStates(session,planId));
    }

    @PostMapping("/scheduler/plans/applystate")
    public void applyStateToPlanEndpoint(@RequestParam("planId") UUID planId, @RequestBody List<CourseStateChange> statesToApply){
        applyStateToPlan(planId, statesToApply);


    }

    private void applyStateToPlan(UUID planId, List<CourseStateChange> statesToApply) {
        Map<UUID, CourseStateChange> stateChanges = new HashMap<>();
        statesToApply.forEach(newState -> {
            stateChanges.put(newState.id(),newState);
        });

        sessionFactory.inTransaction(session -> {
            Plan p = session.find(Plan.class, planId);

            p.getPlannedCourses().forEach(record -> {
                Course course = record.getCourse();
                if(stateChanges.containsKey(course.getId())){
                    record.setSemesterPlanned(stateChanges.get(course.getId()).semesterPlanned());
                }
                else {
                    session.remove(record);
                }
            });
        });
    }

    @PostMapping("/scheduler/plans/reset")
    public List<CourseStateChange> resetPlanState(@RequestParam("planId") UUID planId){
        List<CourseStateChange> initialCourses = new ArrayList<>();
        this.sessionFactory.inTransaction(session -> {
            Plan p = session.find(Plan.class,planId);
            p.getPlannedCourses().forEach(record -> {
                Course c = record.getCourse();
                if(c.getRootPrerequisite() == null){
                    initialCourses.add(CourseStateChange.resetToInitiallyAvailable(c.getId()));
                }
                else {
                    initialCourses.add(CourseStateChange.resetState(c.getId()));
                }
                session.remove(record);
            });
        });
        return initialCourses;
    }



}
