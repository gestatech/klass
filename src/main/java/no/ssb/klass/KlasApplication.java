package no.ssb.klass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

// CHECKSTYLE:OFF
@SpringBootApplication
public class KlasApplication extends SpringBootServletInitializer {
    // TODO kmgv if using embedded container (e.g. Tomcat) remove below method and extends SpringBootServletInitializer
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlasApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(KlasApplication.class, args);
    }
}
// CHECKSTYLE:ON