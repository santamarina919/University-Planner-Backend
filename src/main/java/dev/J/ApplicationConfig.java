package dev.J;


import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@ComponentScan(basePackages = "dev.J")
@Configuration
public class ApplicationConfig {

    private static final String DATABASE_URL = "DATABASE_URL";

    private static final String DATABASE_USERNAME = "DATABASE_USERNAME";

    private static final String DATABASE_PASSWORD = "DATABASE_PASSWORD";


    @Bean
    SessionFactory sessionFactory(){

        var factory = new HibernatePersistenceConfiguration("planuni")
                .managedClass(Campus.class)
                .jdbcUrl(System.getenv(DATABASE_URL))
                .jdbcCredentials(System.getenv(DATABASE_USERNAME),System.getenv(DATABASE_PASSWORD))
                .schemaToolingAction(Action.CREATE)
                .showSql(true,true,true)
                .createEntityManagerFactory();


        return factory;
    }


}
