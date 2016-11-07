package no.ssb.klass.rest;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.SubscriberService;

public class MockConfig {
    @Autowired
    private ClassificationService classificationService;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private StatisticsService statisticsService;

    @Bean
    public ClassificationService classificationService() {
        return Mockito.mock(ClassificationService.class);
    }

    @Bean
    public SubscriberService subscriberService() {
        return Mockito.mock(SubscriberService.class);
    }

    @Bean
    public SearchService searchService() {
        return Mockito.mock(SearchService.class);
    }

    @Bean
    public ClassificationController classificationController() {
        return new ClassificationController(classificationService, subscriberService, searchService, statisticsService);
    }

    @Bean
    public StatisticsService statisticsService() {
        return Mockito.mock(StatisticsService.class);
    }
}
