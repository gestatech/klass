package no.ssb.klass.core.controllers.monitor;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.designer.ui.LoginUI;
import no.ssb.klass.rest.util.RestConstants;

/**
 * @author Mads Lundemo, SSB.
 */
@Controller
@RequestMapping(MonitorController.PATH)
public class MonitorController {
    public static final String PATH = "/monitor/";
    private static final String DATABASE_TILKOBLING = "Database tilkobling";
    private static final String REST_API = "Rest API";
    private static final String KLASS_FORVALTNING = "Klass Forvaltning";

    @Value("${info.build.version:Unknown}")
    private String version;

    @Autowired
    private UserRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(HttpServletRequest request, ModelMap model) {

        List<MonitorStatus> statusList = new LinkedList<>();
        statusList.add(testDatabaseConnection());
        statusList.add(testRestAPI(request));
        statusList.add(testForvaltning(request));

        model.addAttribute("version", version);
        model.addAttribute("statusList", statusList);

        return "monitor";
    }

    private MonitorStatus testDatabaseConnection() {
        try {
            long count = repository.count();
            return new MonitorStatus(DATABASE_TILKOBLING, true,
                    "Alt OK! (fant " + count + " brukere med test spørring)");
        } catch (Exception e) {
            return new MonitorStatus(DATABASE_TILKOBLING, false, "FEIL: " + e.getMessage());
        }
    }

    private MonitorStatus testRestAPI(HttpServletRequest request) {
        try {
            String currentUrl = getCurrentUrl(request);

            String testUrl = currentUrl + RestConstants.REST_PREFIX + "/classifications";
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return new MonitorStatus(REST_API, true,
                        "Alt OK! (" + testUrl + " Returnerte status 200)");
            } else {
                return new MonitorStatus(REST_API, false,
                        " FEIL: Uventet svar (" + testUrl + "Returnerte status " + responseCode + ")");
            }
        } catch (Exception e) {
            return new MonitorStatus(REST_API, false, "Feil ved testing :" + e.getMessage());
        }
    }

    private MonitorStatus testForvaltning(HttpServletRequest request) {
        try {
            String currentUrl = getCurrentUrl(request);

            String testUrl = currentUrl + LoginUI.PATH;
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return new MonitorStatus(KLASS_FORVALTNING, true,
                        "Alt OK! (" + testUrl + " Returnerte status 200)");
            } else {
                return new MonitorStatus(KLASS_FORVALTNING, false,
                        " FEIL: Uventet svar (" + testUrl + "Returnerte status " + responseCode + ")");
            }
        } catch (Exception e) {
            return new MonitorStatus(KLASS_FORVALTNING, false, "Feil ved testing :" + e.getMessage());
        }
    }

    private static String getCurrentUrl(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
            URL url = new URL(request.getRequestURL().toString());
            String host = url.getHost();
            String userInfo = url.getUserInfo();
            String scheme = url.getProtocol();
            int port = url.getPort();
            String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
            String query = (String) request.getAttribute("javax.servlet.forward.query_string");

            URI uri = new URI(scheme, userInfo, host, port, path, query, null);
            return uri.toString();

    }

}
