package no.ssb.klass.rest.applicationtest;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Mads Lundemo, SSB.
 */
public class RestApiCodeIntegrationTest extends AbstractRestApiApplicationTest {
// @formatter:off
    @Test
    public void restServiceCodesJSON() {
        given().port(port).accept(ContentType.JSON).param("from", "2014-01-01").param("to", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(5))
                //1
                .body("codes[0].code", equalTo("0101"))
                .body("codes[0].level", equalTo("1"))
                .body("codes[0].name", equalTo("Halden"))
                .body("codes[0].shortName", equalTo(""))
                .body("codes[0].presentationName", equalTo(""))
                .body("codes[0].validFrom", equalTo("2014-01-01"))
                .body("codes[0].validTo", equalTo("2015-01-01"))
                //2
                .body("codes[1].code", equalTo("0104"))
                .body("codes[1].level", equalTo("1"))
                .body("codes[1].name", equalTo("Moss"))
                .body("codes[1].shortName", equalTo(""))
                .body("codes[1].presentationName", equalTo(""))
                .body("codes[1].validFrom", equalTo("2014-01-01"))
                .body("codes[1].validTo", equalTo("2015-01-01"));
        //...
    }

    @Test
    public void restServiceCodesXML() {
        given().port(port).accept(ContentType.XML).param("from", "2014-01-01").param("to", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.XML)
                .body("codeList.codeItem.size()", equalTo(5))
                //1
                .body("codeList.codeItem[0].code", equalTo("0101"))
                .body("codeList.codeItem[0].level", equalTo("1"))
                .body("codeList.codeItem[0].name", equalTo("Halden"))
                .body("codeList.codeItem[0].shortName", equalTo(""))
                .body("codeList.codeItem[0].presentationName", equalTo(""))
                .body("codeList.codeItem[0].validFrom", equalTo("2014-01-01"))
                .body("codeList.codeItem[0].validTo", equalTo("2015-01-01"))
                //2
                .body("codeList.codeItem[1].code", equalTo("0104"))
                .body("codeList.codeItem[1].level", equalTo("1"))
                .body("codeList.codeItem[1].name", equalTo("Moss"))
                .body("codeList.codeItem[1].shortName", equalTo(""))
                .body("codeList.codeItem[1].presentationName", equalTo(""))
                .body("codeList.codeItem[1].validFrom", equalTo("2014-01-01"))
                .body("codeList.codeItem[1].validTo", equalTo("2015-01-01"));
        //...
    }

    @Test
    public void restServiceCodesCSV() {
        given().port(port).accept(CONTENT_TYPE_CSV).param("from", "2014-01-01").param("to", "2015-01-01")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(CONTENT_TYPE_CSV)

                .content(containsString(
                        "code,level,name,shortName,presentationName,validFrom,validTo\n"
                                + "0101,1,Halden,,,2014-01-01,2015-01-01\n"
                                + "0104,1,Moss,,,2014-01-01,2015-01-01\n"
                                + "0301,1,Oslo,,,2014-01-01,2015-01-01\n"
                                + "1739,1,\"Raarvihke Røyrvik\",,,2014-01-01,2015-01-01\n"
                                + "1939,1,\"Omasvuotna Storfjord Omasvuonon\",,,2014-01-01,2015-01-01"
                ));

    }

    @Test
    public void restServiceCodesRangeNoWildCardsJSON() {
        given().port(port).accept(ContentType.JSON).param("from", "2014-01-01")
                .param("selectCodes", "0100-0400")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(3))
                .body("codes[0].code", equalTo("0101"))
                .body("codes[1].code", equalTo("0104"))
                .body("codes[2].code", equalTo("0301"));

    }
    @Test
    public void restServiceCodesRangeWithWildCardsJSON() {
        given().port(port).accept(ContentType.JSON).param("from", "2014-01-01")
                .param("selectCodes", "010*-0400, 193*")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(4))
                .body("codes[0].code", equalTo("0101"))
                .body("codes[1].code", equalTo("0104"))
                .body("codes[2].code", equalTo("0301"))
                .body("codes[3].code", equalTo("1939"));

    }
    @Test
    public void restServiceCodesOutOfRangeJSON() {
        given().port(port).accept(ContentType.JSON).param("from", "2014-01-01")
                .param("selectCodes", "210*-2400")
                .get(REQUEST_WITH_ID_AND_CODES, kommuneinndeling.getId())
//                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("codes.size()", equalTo(0));
    }


// @formatter:on
}
