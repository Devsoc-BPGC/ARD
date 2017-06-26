package com.macbitsgoa.ard.interfaces;


import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.Assert.assertEquals;


/**
 * Tests for NavigationDrawerListener.
 * @author Rushikesh Jogdand
 */

public class NavigationDrawerListenerTest {


    @Test
    public void testEssentialMethodExists() throws Exception{
        boolean exists = false;
        for (Method memberMethod :
                NavigationDrawerListener.class.getMethods()) {
            if (Objects.equals(memberMethod.getName(), "onDataChange")){
                exists = true;
            }
        }
        assertEquals("onDataChange is essential but does not exist in NavigationDrawerListener", exists, true);
    }
}
