package no.ssb.klass.core.config;

/**
 * Profile name constants
 * 
 * @author Mads Lundemo, SSB.
 */
public final class ConfigurationProfiles {
    private ConfigurationProfiles() {
        // Utility class
    }

    /**
     * Profile intended for production environment (all test features should be excluded).
     */
    public static final String PRODUCTION = "production";

    /**
     * Profile to be used during development, skips indexing classifications for search at startup
     */
    public static final String SKIP_INDEXING = "skip-indexing";

    /**
     * Profile to be used during development, imports only a small number of classifications from Stabas
     */
    public static final String SMALL_IMPORT = "small-import";

    /**
     * Profile that disables Active Directory login so the application can be ran offline.
     */
    public static final String AD_AUTHENTICATE_OFFLINE = "ad-offline";

    /**
     * This profile makes use of the h2 database implementation and store data locally.
     */
    public static final String H2 = "h2";

    /**
     * Profile to be used during development, emails are logged and not sent via mailserver
     */
    public static final String MOCK_MAILSERVER = "mock-mailserver";

    /**
     * This profile makes use of the h2 database in memory feature, all data will be lost on restart.
     */
    public static final String H2_INMEMORY = "h2-inmemory";
}