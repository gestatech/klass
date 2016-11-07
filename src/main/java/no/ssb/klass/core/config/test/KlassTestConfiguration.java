package no.ssb.klass.core.config.test;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.config.production.KlassConfiguration;

@Configuration
@Profile("!" + ConfigurationProfiles.PRODUCTION)
public class KlassTestConfiguration extends KlassConfiguration {

    @Bean
    @Profile({ ConfigurationProfiles.H2, ConfigurationProfiles.H2_INMEMORY })
    ServletRegistrationBean h2servletRegistration() {
        // H2 console (database) is reached at http://localhost:8080/console
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
}