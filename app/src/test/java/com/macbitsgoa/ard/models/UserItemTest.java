package com.macbitsgoa.ard.models;

import com.macbitsgoa.ard.keys.UserItemKeys;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for model UserItem to ensure it's usable to use with firebase db.
 *
 * @author Rushikesh Jogdand
 */
public class UserItemTest {
    @Test
    public void testFieldNamesMatchFDR() throws NoSuchFieldException {
        assertNotNull(UserItem.class.getDeclaredField(UserItemKeys.NAME));
        assertNotNull(UserItem.class.getDeclaredField(UserItemKeys.EMAIL));
        assertNotNull(UserItem.class.getDeclaredField(UserItemKeys.PHOTO_URL));
    }

    @Test
    public void testUsability() {
        UserItem userItem = new UserItem();
        userItem.setName("Name Surname");
        userItem.setEmail("username@domain.com");
        userItem.setPhotoUrl("https://www.server.in/pic.jpg");
        assertEquals("Name Surname", userItem.getName());
        assertEquals("username@domain.com", userItem.getEmail());
        assertEquals("https://www.server.in/pic.jpg", userItem.getPhotoUrl());
        assertEquals("name=Name Surname, email=username@domain.com, photoUrl=https://www.server.in/pic.jpg",
                userItem.toString());
    }
}
