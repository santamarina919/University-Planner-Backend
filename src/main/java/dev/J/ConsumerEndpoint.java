package dev.J;


import dev.J.Entities.Consumer;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ConsumerEndpoint {

    private final SessionFactory factory;

    public record SignUpBody(String email, String password, String firstName){}

    @PostMapping("/consumer/signup")
    public void create(@RequestBody SignUpBody body){
        String hashedPw = BCrypt.hashpw(body.email,BCrypt.gensalt());
        Consumer c = new Consumer();
        c.setEmail(body.email);
        c.setPassword(hashedPw);
        c.setFirstName(body.firstName);

        factory.inTransaction(entityManager -> {
            entityManager.persist(c);
        });
    }
}
