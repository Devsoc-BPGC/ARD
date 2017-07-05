package com.macbitsgoa.ard.fragments;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.interfaces.PostListener;

/**
 * Base class for fragments in HomeFragment.
 * This has a single method {@link HomePostFragment#post()} that the HomeFragment can call
 * when send button is pressed.
 *
 * @author Vikramaditya Kukreja
 */
public class HomePostFragment extends Fragment {

    /**
     * Listener for {@link PostListener}.
     */
    protected PostListener postListener;

    public void post() {
        //Method that will be overridden.
    }

    /**
     * Get resources integers.
     *
     * @param id Resource id to use.
     * @return int value of this resource.
     */
    public int getInteger(final int id) {
        return getResources().getInteger(id);
    }

    public void setPostListener(final PostListener postListener) {
        this.postListener = postListener;
    }

    /**
     * Convenience method to get root database reference.
     *
     * @return Root database reference.
     */
    public DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }
}
