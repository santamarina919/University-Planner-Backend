package dev.J;

import dev.J.Entities.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.ott.DefaultGenerateOneTimeTokenRequestResolver;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ext.jdk8.BaseScalarOptionalDeserializer;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Degree> allDegrees(InputStream inputStream) {
        return supportedUniversities.stream()
                .map(this::fetchDegrees)
                .flatMap(List::stream)
                .toList();
    }

    @GetMapping("/scheduler/universities/degrees/{campusId}")
    public List<Degree> degreesFrom(@PathVariable String campusId){
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


            List<Degree> degrees = body.degreeIds.stream()
                    .map(id -> session.find(Degree.class,id))
                    .toList();

            Plan p = new Plan();
            p.setConsumer(owner);
            p.setName(body.name);
            p.getRootDegrees().addAll(degrees);
            session.persist(p);
            return p.getId();
        });
        return planId;
    }
    //todo init plan endpoints
    //todo courses states


    //todo add courses
    @PostMapping("/scheduler/plans/add")
    public ResponseEntity<Void> addCourse(@RequestParam("planId") UUID planId, @RequestParam("courseId") UUID courseId, @RequestParam("semester") int semester){
        boolean wasSuccessFull = sessionFactory.fromTransaction(session -> CourseAddition.addCourse(session,planId,courseId,semester));
        return wasSuccessFull ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    //todo remove courses
    @GetMapping("/scheduler/plans/remove")
    public void removeCourse(@RequestParam String planId, @RequestParam String courseId){

    }

}
