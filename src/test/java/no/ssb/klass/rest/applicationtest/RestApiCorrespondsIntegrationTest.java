package no.ssb.klass.rest.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiCorrespondsIntegrationTest extends AbstractRestApiApplicationTest {
// @formatter:off
    @Test
    public void restServiceCorrespondsJSON() {
        given().port(port).accept(ContentType.JSON)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value())
                .body(JSON_CORRESPONDENCES + ".size()", equalTo(3))
                //1
                .body(JSON_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(JSON_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(JSON_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[0].validTo", equalTo(null))
                //...
                //3
                .body(JSON_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(JSON_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(JSON_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(JSON_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(JSON_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(JSON_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(JSON_CORRESPONDENCES + "[2].validTo", equalTo(null));

    }

    @Test
    public void restServiceCorrespondsXML() {
        given().port(port).accept(ContentType.XML)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .contentType(ContentType.XML)
                .statusCode(HttpStatus.OK.value())
                .body(XML_CORRESPONDENCES + ".size()", equalTo(3))
                //1
                .body(XML_CORRESPONDENCES + "[0].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[0].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[0].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].targetCode", equalTo("030101"))
                .body(XML_CORRESPONDENCES + "[0].targetName", equalTo("Gamle Oslo"))
                .body(XML_CORRESPONDENCES + "[0].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[0].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[0].validTo", equalTo(""))
                //...
                //3
                .body(XML_CORRESPONDENCES + "[2].sourceCode", equalTo("0301"))
                .body(XML_CORRESPONDENCES + "[2].sourceName", equalTo("Oslo"))
                .body(XML_CORRESPONDENCES + "[2].sourceShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].targetCode", equalTo("030103"))
                .body(XML_CORRESPONDENCES + "[2].targetName", equalTo("Sagene"))
                .body(XML_CORRESPONDENCES + "[2].targetShortName", equalTo(""))
                .body(XML_CORRESPONDENCES + "[2].validFrom", equalTo("2015-01-01"))
                .body(XML_CORRESPONDENCES + "[2].validTo", equalTo(""));

    }

    @Test
    public void restServiceCorrespondsCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV)
                .param("targetClassificationId", bydelsinndeling.getId())
                .param("from", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CORRESPONDS, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .content(containsString(
                        "sourceCode,sourceName,sourceShortName,targetCode,targetName,targetShortName,validFrom,validTo\n"
                                + "0301,Oslo,,030101,\"Gamle Oslo\",,2015-01-01,\n"
                                + "0301,Oslo,,030102,Grünerløkka,,2015-01-01,\n"
                                + "0301,Oslo,,030103,Sagene,,2015-01-01,"
                ));

    }
// @formatter:on
}
