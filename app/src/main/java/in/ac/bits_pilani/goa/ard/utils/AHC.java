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
     * Default separator to use.
     */
    public static final String SEPARATOR = ".";

    /**
     * Package name of project.
     */
    public static final String PACKAGE_NAME = "in" + SEPARATOR + "ac" + SEPARATOR
            + SEPARATOR + "bits_pilani" + SEPARATOR + "goa" + SEPARATOR + "ard";

    /**
     * default Log tag for project.
     */

    public static final String TAG = "ard" + SEPARATOR + "bits"  + SEPARATOR + "goa";
}
