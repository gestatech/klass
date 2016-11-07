package no.ssb.klass.core.config.production;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import com.vaadin.ui.declarative.Design;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.designer.vaadin.VaadinSpringComponentFactory;

@Configuration
@Profile(ConfigurationProfiles.PRODUCTION)
@EnableAsync
public class KlassConfiguration {

    @Bean
    public VaadinSpringComponentFactory componentFactory() {
        VaadinSpringComponentFactory componentFactory = new VaadinSpringComponentFactory();
        Design.setComponentFactory(componentFactory);
        return componentFactory;
    }

}