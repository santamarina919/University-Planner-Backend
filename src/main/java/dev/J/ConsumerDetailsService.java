package dev.J;

import dev.J.Entities.Consumer;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class ConsumerDetailsService implements UserDetailsService {

    private final SessionFactory sessionFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.sessionFactory.fromSession(
                session -> {
                    return session.bySimpleNaturalId(Consumer.class).load(username);
                }
        );
    }
}
