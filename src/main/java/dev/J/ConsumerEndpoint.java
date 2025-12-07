package dev.J;


import dev.J.Entities.Consumer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ConsumerEndpoint {

    private final DaoAuthenticationProvider authenticationProvider;

    private final PasswordEncoder pwHasher;

    private final SessionFactory factory;

    public record SignUpBody(String email, String password, String firstName){}


    @PostMapping("/consumer/entry/signup")
    public void create(@RequestBody SignUpBody body){
        String hashedPw = pwHasher.encode(body.password);
        Consumer c = new Consumer();
        c.setEmail(body.email);
        c.setPassword(hashedPw);
        c.setFirstName(body.firstName);
        factory.inTransaction(entityManager -> {
            entityManager.persist(c);
        });
    }

    public record LoginBody(String email, String password){}

    @PostMapping("/consumer/entry/login")
    public void login(@RequestBody LoginBody body, HttpServletRequest request){
        Consumer c = factory.fromSession(session -> session.bySimpleNaturalId(Consumer.class).load("j@gmail.com"));
        Authentication authentication = authenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(body.email,body.password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

}
