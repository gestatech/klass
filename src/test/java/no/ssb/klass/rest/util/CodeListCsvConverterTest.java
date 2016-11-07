package no.ssb.klass.rest.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.dto.Code;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.rest.dto.CodeList;
import no.ssb.klass.testutil.ConstantClockSource;
import no.ssb.klass.testutil.TestUtil;

public class CodeListCsvConverterTest {
    private CodeListCsvConverter subject;
    private Date now;

    @Before
    public void setup() {
        now = new Date();
        TimeUtil.setClockSource(new ConstantClockSource(now));
        subject = new CodeListCsvConverter();
    }

    @After
    public void teardown() {
        TimeUtil.revertClockSource();
    }

    @Test
    public void supports() {
        assertEquals(true, subject.supports(CodeList.class));
        assertEquals(false, subject.supports(CodeListCsvConverterTest.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readInternal() {
        subject.readInternal(null, null);
    }

    @Test
    public void writeInternalWithValidRange() throws Exception {
        // given
        final boolean showValidRange = true;
        HttpOutputMessage outputMessage = new MockHttpOutputMessage();

        // when
        subject.writeInternal(createCodeList(showValidRange), outputMessage);

        // then
        assertEquals(
                "code,level,name,shortName,presentationName,validFrom,validTo\n0104,1,Sandefjord,,,2008-01-01,2020-01-01\n",
                outputMessage.getBody().toString());
    }

    @Test
    public void writeInternalWithoutValidRange() throws Exception {
        // given
        final boolean showValidRange = false;
        HttpOutputMessage outputMessage = new MockHttpOutputMessage();

        // when
        subject.writeInternal(createCodeList(showValidRange), outputMessage);

        // then
        assertEquals("code,level,name,shortName,presentationName\n0104,1,Sandefjord,,\n", outputMessage.getBody()
                .toString());
    }

    private CodeList createCodeList(boolean withValidRange) {
        Code code = new Code(TestUtil.createLevel(1), TestUtil.createClassificationItem("0104", "Sandefjord"), DateRange
                .create("2008-01-01", "2020-01-01"), Language.getDefault());
        return new CodeList(",", withValidRange).convert(Arrays.asList(code));
    }
}
