package dev.J.Config;


import dev.J.ConsumerDetailsService;
import dev.J.Entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@ComponentScan(basePackages = "dev.J.**")
public class ApplicationConfig {

    private static final String DATABASE_URL = "DATABASE_URL";

    private static final String DATABASE_USERNAME = "DATABASE_USERNAME";

    private static final String DATABASE_PASSWORD = "DATABASE_PASSWORD";


    @Bean
    SessionFactory sessionFactory(){

        var factory = new HibernatePersistenceConfiguration("university_planner")
                .managedClasses(Address.class,Campus.class, Consumer.class, Course.class, Degree.class,Plan.class, Prerequisite.class, Requirement.class, PlannedCourse.class, PlanDegree.class)
                .jdbcUrl(System.getenv(DATABASE_URL))
                .jdbcCredentials(System.getenv(DATABASE_USERNAME),System.getenv(DATABASE_PASSWORD))
                .schemaToolingAction(Action.CREATE_DROP)
                .showSql(true,true,true)
                .createEntityManagerFactory();


        return factory;
    }

    @Bean
    UserDetailsService consumerDetailsService(SessionFactory factory){
        return new ConsumerDetailsService(factory);
    }

}
