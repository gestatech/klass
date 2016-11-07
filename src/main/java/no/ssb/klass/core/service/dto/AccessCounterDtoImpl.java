package no.ssb.klass.core.service.dto;

public class AccessCounterDtoImpl implements StatisticalEntity {
    private final String entityName;
    private final Long count;
    
    public AccessCounterDtoImpl(String entityName, Long count) {
        this.entityName = entityName;
        this.count = count;
    }

    public String getName() {
        return entityName;
    }

    public Long getCount() {
        return count;
    }
    
    
}
