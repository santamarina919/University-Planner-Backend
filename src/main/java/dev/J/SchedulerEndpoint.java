package dev.J;

import dev.J.Entities.Campus;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                        sessionFactory.fromSession(
                                session -> session.find(Campus.class,id)
                        )
                )
                .toList();
    }

}
