package dev.J;


import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "dev.J")
public class ApplicationConfig {

    private static final String DATABASE_URL = "DATABASE_URL";

    private static final String DATABASE_USERNAME = "DATABASE_USERNAME";

    private static final String DATABASE_PASSWORD = "DATABASE_PASSWORD";


    @Bean
    SessionFactory sessionFactory(){
        var factory = new HibernatePersistenceConfiguration("PlanUni")
                .jdbcUrl(System.getenv(DATABASE_URL))
                .jdbcCredentials(System.getenv(DATABASE_USERNAME),System.getenv(DATABASE_PASSWORD))
                .schemaToolingAction(Action.CREATE_ONLY)
                .showSql(true,true,true)
                .createEntityManagerFactory();

        factory.getSchemaManager().create(true);

        return factory;
    }


}
