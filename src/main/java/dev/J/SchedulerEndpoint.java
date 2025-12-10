package dev.J;

import dev.J.Entities.Campus;
import dev.J.Entities.Consumer;
import dev.J.Entities.Degree;
import dev.J.Entities.Plan;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ext.jdk8.BaseScalarOptionalDeserializer;

import java.util.List;
import java.util.UUID;

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


    public record CreatePlanBody(String name, List<UUID> degreeIds){}

    //todo redirect user to page of the newly created plan
    @PostMapping("/scheduler/plans/create")
    public void createPlan(Authentication auth, @RequestBody CreatePlanBody body){
        sessionFactory.inTransaction(session -> {

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
        });
    }

}
