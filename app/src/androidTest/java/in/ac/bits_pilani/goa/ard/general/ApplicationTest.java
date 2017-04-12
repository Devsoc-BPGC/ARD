package in.ac.bits_pilani.goa.ard.general;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertArrayEquals;

/**
 * Basic tests for the application.
 * @author vikramaditya
 */

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Mock
    Context context;

    @Before
    public void init() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testPackageName() throws Exception {
        String expectedPackageName = "in.ac.bits_pilani.goa.ard.debug";
        assertArrayEquals("Package name doesn't match",
                new String[]{expectedPackageName},
                new String[]{context.getPackageName()});
    }

    @Test
    public void testTargetSdkVersion() throws Exception {
        final int targetSdkVersion = 25;
        assertArrayEquals("Target version incorrect",
                new int[]{targetSdkVersion},
                new int[]{context.getApplicationInfo().targetSdkVersion});
    }

}
