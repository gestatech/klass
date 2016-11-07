package no.ssb.klass.rest.applicationtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.CorrespondenceTableRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.rest.applicationtest.config.ApplicationTestConfig;
import no.ssb.klass.rest.applicationtest.providers.TestDataProvider;
import no.ssb.klass.rest.util.RestConstants;
import no.ssb.klass.testutil.ConstantClockSource;
import no.ssb.klass.testutil.TestUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ApplicationTestConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@DirtiesContext
@ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
public abstract class AbstractRestApiApplicationTest {

    public static final String CONTENT_TYPE_CSV = "text/csv";

    public static final String REQUEST = RestConstants.REST_PREFIX + "/classifications";
    public static final String REQUEST_WITH_ID = REQUEST + "/{classificationId}";
    public static final String REQUEST_SEARCH = REQUEST + "/search";
    public static final String REQUEST_WITH_ID_AND_CODES = REQUEST + "/{classificationId}/codes";
    public static final String REQUEST_WITH_ID_AND_CODES_AT = REQUEST + "/{classificationId}/codesAt";
    public static final String REQUEST_WITH_ID_AND_VARIANT = REQUEST + "/{classificationId}/variant";
    public static final String REQUEST_WITH_ID_AND_VARIANT_AT = REQUEST + "/{classificationId}/variantAt";
    public static final String REQUEST_WITH_ID_AND_CORRESPONDS = REQUEST + "/{classificationId}/corresponds";
    public static final String REQUEST_WITH_ID_AND_CORRESPONDS_AT = REQUEST + "/{classificationId}/correspondsAt";

    public static final String REQUEST_SSB_SECTION = RestConstants.REST_PREFIX + "/ssbsections";

    public static final String REQUEST_CLASSIFICATION_FAMILY = RestConstants.REST_PREFIX + "/classificationfamilies";
    public static final String REQUEST_CLASSIFICATION_FAMILY_WITH_ID = REQUEST_CLASSIFICATION_FAMILY
            + "/{classificationfamilyId}";

    public static final String REQUEST_WITH_ID_AND_CHANGES = REQUEST + "/{classificationId}/changes";

    public static final String JSON_SEARCH_RESULTS = "_embedded.searchResults";
    public static final String JSON_SEARCH_RESULT1 = "_embedded.searchResults[0]";
    public static final String JSON_SEARCH_RESULT2 = "_embedded.searchResults[1]";

    public static final String XML_SEARCH_RESULTS = "PagedResources.contents.content";
    public static final String XML_SEARCH_RESULT1 = "PagedResources.contents.content[0]";
    public static final String XML_SEARCH_RESULT2 = "PagedResources.contents.content[1]";

    public static final String JSON_CLASSIFICATIONS = "_embedded.classifications";
    public static final String JSON_CLASSIFICATION1 = "_embedded.classifications[0]";
    public static final String JSON_CLASSIFICATION2 = "_embedded.classifications[1]";
    public static final String JSON_CLASSIFICATION3 = "_embedded.classifications[2]";

    public static final String XML_CLASSIFICATIONS = "PagedResources.contents.content";
    public static final String XML_CLASSIFICATION1 = "PagedResources.contents.content[0]";
    public static final String XML_CLASSIFICATION2 = "PagedResources.contents.content[1]";
    public static final String XML_CLASSIFICATION3 = "PagedResources.contents.content[2]";

    public static final String JSON_PAGE = "page";
    public static final String XML_PAGE = "PagedResources.page";

    public static final String JSON_LINKS = "_links";
    public static final String XML_LINKS = "PagedResources.links";

    public static final String JSON_CODES = "codes";
    public static final String XML_CODES = "codeList.codeItem";

    public static final String XML_CORRESPONDENCES = "correspondenceItemList.correspondenceItem";
    public static final String JSON_CORRESPONDENCES = "correspondenceItems";

    public static final int PAGE_SIZE = 20;

    public static final String CHANGED_SINCE_NEW_DATE = "2015-10-31T03:00:00.000-0200";
    public static final String CHANGED_SINCE_OLD_DATE = "2000-10-30T01:00:00.000-0200";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected static ClassificationSeries kommuneinndeling;
    protected static ClassificationSeries bydelsinndeling;
    protected static ClassificationSeries familieGrupperingCodelist;
    protected static ClassificationFamily classificationFamily;
    /**
     * "Hack" to keep data between tests and reinitialize in new test suites NOTE: For these tests to work without
     * "hacks" we would have to start writing more test friendly code so that we can clear database and search index
     * using test utils (main problem is search index)
     * <p>
     * It is possible to use reflectionUtils and get the index list and do a clear, but reflectionUtils uses strings and
     * is prone to refactoring
     */
    protected static boolean testDataInitialized;

    @Autowired
    protected ClassificationService classificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired
    private ClassificationSeriesRepository seriesRepository;
    @Autowired
    private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired
    private TransactionTemplate template;
    @Value("${local.server.port}")
    protected int port;

    @Before
    public void prepareTestData() throws ParseException {
        if (!testDataInitialized) {
            User user = userRepository.save(TestUtil.createUser());
            TimeUtil.setClockSource(new ConstantClockSource(dateFormat.parse(CHANGED_SINCE_OLD_DATE)));
            classificationFamily = classificationFamilyRepository.save(TestUtil
                    .createClassificationFamily("Befolkning"));
            kommuneinndeling = TestDataProvider.createClassificationKommuneinndeling();
            kommuneinndeling.setContactPerson(user);
            classificationFamily.addClassificationSeries(kommuneinndeling);
            TimeUtil.setClockSource(new ConstantClockSource(dateFormat.parse(CHANGED_SINCE_NEW_DATE)));
            bydelsinndeling = TestDataProvider.createClassificationBydelsinndeling();
            bydelsinndeling.setContactPerson(user);
            classificationFamily.addClassificationSeries(bydelsinndeling);

            familieGrupperingCodelist = TestDataProvider.createFamiliegrupperingCodelist(user);
            classificationFamily.addClassificationSeries(familieGrupperingCodelist);
            classificationService.saveAndIndexClassification(familieGrupperingCodelist);

            kommuneinndeling = classificationService.saveAndIndexClassification(kommuneinndeling);
            bydelsinndeling = classificationService.saveAndIndexClassification(bydelsinndeling);


            correspondenceTableRepository.save(TestDataProvider.createAndAddCorrespondenceTable(kommuneinndeling,
                    bydelsinndeling));
            correspondenceTableRepository.save(TestDataProvider.createAndAddChangeCorrespondenceTable(
                    kommuneinndeling));
            TimeUtil.setClockSource(new ConstantClockSource(dateFormat.parse(CHANGED_SINCE_OLD_DATE)));
            classificationService.saveAndIndexClassification(kommuneinndeling);
            TimeUtil.revertClockSource();

            TransactionStatus transaction = template.getTransactionManager().getTransaction(null);
            seriesRepository.updateClassificationLastModified(bydelsinndeling.getId(), bydelsinndeling
                    .getLastModified());
            template.getTransactionManager().commit(transaction);
            testDataInitialized = true;

        }
    }

    @AfterClass
    public static void reset() {
        testDataInitialized = false;
    }

}
