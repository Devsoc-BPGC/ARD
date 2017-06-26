package com.macbitsgoa.ard.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for AHC.
 * @author vikramaditya
 */

public class AhcTest {


    @Test
    public void testClassName() throws Exception{
        String expected = "AHC";
        assertEquals("Class name is wrong", expected, AHC.class.getSimpleName());
    }
}
