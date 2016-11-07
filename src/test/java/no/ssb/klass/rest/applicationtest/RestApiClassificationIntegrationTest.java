package no.ssb.klass.rest.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

import no.ssb.klass.rest.applicationtest.providers.TestDataProvider;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiClassificationIntegrationTest extends AbstractRestApiApplicationTest {
// @formatter:off
    @Test
    public void restServiceReturnClassification() {
        String urlParts = REQUEST + "/" + kommuneinndeling.getId();

        given().port(port).accept(ContentType.JSON)
                .get(REQUEST_WITH_ID, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(ContentType.JSON)
                // classification
                .assertThat().body("name", equalTo(TestDataProvider.KOMMUNEINNDELING_NAVN_NO))
                .assertThat().body("description", equalTo(TestDataProvider.KOMMUNEINNDELING_BESKRIVELSE_NO))
                .assertThat().body("lastModified", notNullValue())
                .assertThat().body(JSON_LINKS + ".self.href", containsString("classifications/" + kommuneinndeling.getId()))
                // versions
                .assertThat().body("versions.size", equalTo(2))
                //
                .assertThat().body("versions[0].name", equalTo("Kommuneinndeling 2014"))
                .assertThat().body("versions[0]._links.self.href", containsString("versions/"))
                //
                .assertThat().body("versions[1].name", equalTo("Kommuneinndeling 2012"))
                .assertThat().body("versions[1]._links.self.href", containsString("versions/"))
                // links
                .body(JSON_LINKS + ".self.href", containsString(urlParts))
                .body(JSON_LINKS + ".codes.href", containsString(urlParts + "/codes"
                        + "{?from=<yyyy-MM-dd>,to=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}"))
                .body(JSON_LINKS + ".codesAt.href", containsString(urlParts + "/codesAt"
                        + "{?date=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}"))
                .body(JSON_LINKS + ".variant.href", containsString(urlParts + "/variant"
                        + "{?variantName,from=<yyyy-MM-dd>,to=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}"))
                .body(JSON_LINKS + ".variantAt.href", containsString(urlParts + "/variantAt"
                        + "{?variantName,date=<yyyy-MM-dd>,csvSeparator,level,selectCodes,presentationNamePattern}"))
                .body(JSON_LINKS + ".corresponds.href", containsString(urlParts + "/corresponds"
                        + "{?targetClassificationId,from=<yyyy-MM-dd>,to=<yyyy-MM-dd>,csvSeparator}"))
                .body(JSON_LINKS + ".correspondsAt.href", containsString(urlParts + "/correspondsAt"
                        + "{?targetClassificationId,date=<yyyy-MM-dd>,csvSeparator}"))
                .body(JSON_LINKS + ".changes.href", containsString(urlParts + "/changes"
                        + "{?from=<yyyy-MM-dd>,to=<yyyy-MM-dd>,csvSeparator}"));
    }
// @formatter:on
}