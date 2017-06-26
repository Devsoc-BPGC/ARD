package com.macbitsgoa.ard.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;

@RunWith(MockitoJUnitRunner.class)
public class ViewPagerAdapterTest {

    @Mock
    public FragmentManager fragmentManager;
    @Mock
    public Fragment sampleFragment;
    private ViewPagerAdapter viewPagerAdapter;

    @Before
    public void init() {
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
    }

    @Test
    public void testNullGetItem() {
        String[] expected = new String[]{
                "Index: 2147483647, Size: 0",
        };
        try {
            viewPagerAdapter.getItem(Integer.MAX_VALUE);
        } catch (IndexOutOfBoundsException e) {
            assertArrayEquals(expected, new String[]{e.getMessage()});
        }
    }

    @Test
    public void testNullItemGetPageTitle() {
        String[] expected = new String[]{
                "Index: 2147483647, Size: 0",
        };
        try {
            viewPagerAdapter.getPageTitle(Integer.MAX_VALUE);
        } catch (IndexOutOfBoundsException e) {
            assertArrayEquals(expected, new String[]{e.getMessage()});
        }
    }

    @Test
    public void testCount() {
        int[] expected = new int[]{
                2,
        };
        viewPagerAdapter.addFragment(sampleFragment, "frag1");
        viewPagerAdapter.addFragment(sampleFragment, "frag2");
        assertArrayEquals(expected, new int[]{viewPagerAdapter.getCount()});
    }

    @Test
    public void testZeroCount() {
        int[] expected = new int[]{
                0,
        };
        assertArrayEquals(expected, new int[]{viewPagerAdapter.getCount()});
    }
}
