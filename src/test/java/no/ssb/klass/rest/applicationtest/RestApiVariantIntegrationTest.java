package no.ssb.klass.rest.applicationtest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiVariantIntegrationTest extends AbstractRestApiApplicationTest {
// @formatter:off
    @Test
    public void restServiceVariantJSON() {
        given().port(port).accept(ContentType.JSON).param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                //1
                .body("codes[0].code", equalTo("A"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Enpersonfamilie"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFrom", equalTo("2014-01-01"))
                .body("codes[0].validTo", equalTo(null))
                //2
                .body("codes[1].code", equalTo("A_"))
                .body("codes[1].level", equalTo("2"))
                .body("codes[1].name", equalTo("Enpersonfamilie"))
                .body("codes[1].shortName", equalTo(""))
                .body("codes[1].presentationName", equalTo(""))
                .body("codes[1].validFrom", equalTo("2014-01-01"))
                .body("codes[1].validTo", equalTo(null))
                //...
                //5
                .body("codes[4].code", equalTo("BB"))
                .body("codes[4].level", equalTo("2"))
                .body("codes[4].name", equalTo("Ektepar uten barn 0-17 år"))
                .body("codes[4].shortName", equalTo(""))
                .body("codes[4].presentationName", equalTo(""))
                .body("codes[4].validFrom", equalTo("2014-01-01"))
                .body("codes[4].validTo", equalTo(null));


    }

    @Test
    public void restServiceVariantXML() {
        given().port(port).accept(ContentType.XML).param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body(XML_CODES + ".size()", equalTo(5))
                //1
                .body(XML_CODES + "[0].code", equalTo("A"))
                .body(XML_CODES + "[0].level", equalTo("1"))
                .body(XML_CODES + "[0].name", equalTo("Enpersonfamilie"))
                .body(XML_CODES + "[0].shortName", equalTo(""))
                .body(XML_CODES + "[0].presentationName", equalTo(""))
                .body(XML_CODES + "[0].validFrom", equalTo("2014-01-01"))
                .body(XML_CODES + "[0].validTo", equalTo(""))
                //2
                .body(XML_CODES + "[1].code", equalTo("A_"))
                .body(XML_CODES + "[1].level", equalTo("2"))
                .body(XML_CODES + "[1].name", equalTo("Enpersonfamilie"))
                .body(XML_CODES + "[1].shortName", equalTo(""))
                .body(XML_CODES + "[1].presentationName", equalTo(""))
                .body(XML_CODES + "[1].validFrom", equalTo("2014-01-01"))
                .body(XML_CODES + "[1].validTo", equalTo(""))
                //...
                //5
                .body(XML_CODES + "[4].code", equalTo("BB"))
                .body(XML_CODES + "[4].level", equalTo("2"))
                .body(XML_CODES + "[4].name", equalTo("Ektepar uten barn 0-17 år"))
                .body(XML_CODES + "[4].shortName", equalTo(""))
                .body(XML_CODES + "[4].presentationName", equalTo(""))
                .body(XML_CODES + "[4].validFrom", equalTo("2014-01-01"))
                .body(XML_CODES + "[4].validTo", equalTo(""));


    }

    @Test
    public void restServiceVariantCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV).param("variantName", "Variant - Tilleggsinndeling for familier")
                .param("from", "2014-01-01")
                .get(REQUEST_WITH_ID_AND_VARIANT, familieGrupperingCodelist.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(CONTENT_TYPE_CSV)
                .content(containsString(
                        "code,level,name,shortName,presentationName,validFrom,validTo\n"
                                + "A,1,Enpersonfamilie,,,2014-01-01,\n"
                                + "A_,2,Enpersonfamilie,,,2014-01-01,\n"
                                + "B,1,Ektepar,,,2014-01-01,\n"
                                + "BA,2,\"Ektepar med barn (yngste barn 0-17 år)\",,,2014-01-01,\n"
                                + "BB,2,\"Ektepar uten barn 0-17 år\",,,2014-01-01,\n"
                ));

    }
// @formatter:on
}
