package dev.J.Config;

import jakarta.servlet.ServletRegistration;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {

    protected Class<?> @Nullable [] getRootConfigClasses() {
        return null;
    }

    protected Class<?> @Nullable [] getServletConfigClasses() {
        return new Class[]{ApplicationConfig.class,WebConfiguration.class,SercurityConfig.class};
    }


    protected String[] getServletMappings() {
        return new String[]{"/*"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("enableLoggingRequestDetails", "true");
    }
}
