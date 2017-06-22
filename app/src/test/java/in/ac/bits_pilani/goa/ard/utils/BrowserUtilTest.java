package in.ac.bits_pilani.goa.ard.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test for BrowserUtil Class.
 * @author Rushikesh Jogdand
 */

public class BrowserUtilTest {
    @Test
    public void testClassName() {
        String expected = "BrowserUtil";
        assertEquals("Class name is wrong", expected, BrowserUtil.class.getSimpleName());
    }

    @Test
    public void testConstValues() {
        assertEquals(24, BrowserUtil.ACTION_BUTTON_HEIGHT);
        assertEquals(24, BrowserUtil.CLOSE_BUTTON_HEIGHT);
        assertEquals(24, BrowserUtil.CLOSE_BUTTON_WIDTH);
    }
}
