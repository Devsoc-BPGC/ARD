package com.macbitsgoa.ard.fragments;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FaqFragmentTest {

    private FaqFragment faqFragment;

    @Before
    public void init() {
        faqFragment = FaqFragment.newInstance(null);
    }

    @Test
    public void testOnAttachNoInterfaceContext() {
        Context context = mock(Context.class);

        String expected = context.toString() + " must implement OnFragmentInteractionListener";

        try {
            faqFragment.onAttach(context);
        } catch (RuntimeException e) {
            assertEquals(expected, e.getMessage());
        }
    }
}
