package no.ssb.klass.rest.dto.hal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;

import no.ssb.klass.core.service.search.SearchResult;
import no.ssb.klass.rest.ClassificationController;

@Relation(collectionRelation = "searchResults")
public class SearchResultResource extends KlassResource {
    private final String name;
    private final String snippet;
    private final Long searchScore;

    public SearchResultResource(SearchResult searchResult) {
        this.name = searchResult.getResourceName();
        this.snippet = searchResult.getSnippet();
        this.searchScore = searchResult.getSearchScore();
        addLink(createSelfLink(searchResult.getResourceId()));
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null)).withSelfRel();
    }

    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public Long getSearchScore() {
        return searchScore;
    }
}
