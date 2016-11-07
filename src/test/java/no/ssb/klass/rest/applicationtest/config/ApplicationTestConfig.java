package no.ssb.klass.rest.applicationtest.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import no.ssb.klass.core.service.UserService;
import no.ssb.klass.designer.UserServiceMock;

/**
 * @author Mads Lundemo, SSB.
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "no.ssb.klass.core.service",
        "no.ssb.klass.rest",
        "no.ssb.klass.core.repository" })
@EntityScan(basePackages = "no.ssb.klass.core.model")
public class ApplicationTestConfig {

    @Bean
    @Qualifier("user-test")
    public UserService userService() {
        return new UserServiceMock();
    }

    @Configuration
    static class TestSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/**").permitAll();
        }
    }
}
