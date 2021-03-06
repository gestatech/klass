package no.ssb.klass.rest.dto.hal;

import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation = "ssbSections")
public class SsbSectionResource extends KlassResource {
    private final String name;

    public SsbSectionResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
