package in.ac.bits_pilani.goa.ard.utils;

/**
 * Helper class for ARD
 * @author vikramaditya
 */
public class AHC {

    @Override
    public String toString() {
        return AHC.class.getSimpleName();
    }

    /**
     * Package name of project.
     */
    public static final String PACKAGE_NAME = "in.ac.bits_pilani.goa.ard";

    /**
     * Default Log tag for project.
     */
    public static final String TAG = "ard.bits.goa";

    /**
     * Name of Realm database.
     */
    public static final String REALM_ARD_DATABASE = "REALM_ARD_DATABASE";

    /**
     * Version of Realm database.
     */
    public static final int REALM_ARD_DATABASE_SCHEMA = 0;
}
