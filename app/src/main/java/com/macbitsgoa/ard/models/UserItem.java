package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.macbitsgoa.ard.utils.AHC;

/**
 * Model class 'UserItem'.
 * This data is stored in firebase directory {@link AHC#FDR_USERS} with uid
 * (obtained by {@link FirebaseUser#getUid()}) as key.
 * A user has read access to data of all users, and write access to his/her info.
 *
 * @author Rushikesh Jogdand
 */
public class UserItem implements Comparable<UserItem> {

    /**
     * User unique id.
     */
    private String uid;

    /**
     * UserItem name (full name initially obtained from {@link FirebaseUser#getDisplayName()}).
     */
    private String name;

    /**
     * Email Id from {@link FirebaseUser#getEmail()}.
     */
    private String email;

    /**
     * Photo url from {@link FirebaseUser#getPhotoUrl()}.
     */
    private String photoUrl;

    /**
     * Extra description field.
     */
    private String desc;

    public UserItem() {
        this.uid = "";
        email = "";
        photoUrl = "";
        name = "";
        desc = "";
    }

    public UserItem(@NonNull final String uid, @Nullable final String name,
                    @Nullable final String email, @Nullable final String photoUrl,
                    @Nullable final String desc) {
        this.uid = uid;
        setName(name);
        setEmail(email);
        setPhotoUrl(photoUrl);
        setDesc(desc);
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid == null ? "" : uid;
    }

    @NonNull
    public String getDesc() {
        return desc;
    }

    private void setDesc(final String desc) {
        this.desc = desc == null ? "" : desc;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name == null ? "" : name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email == null ? "" : email;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@Nullable final String photoUrl) {
        this.photoUrl = photoUrl == null ? "" : photoUrl;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof UserItem && ((UserItem) obj).getUid().equals(this.uid);
    }

    @Override
    public String toString() {
        return "UserItem{"
                + "uid='" + uid + '\''
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", photoUrl='" + photoUrl + '\''
                + ", desc='" + desc
                + '}';
    }

    @Override
    public int compareTo(@NonNull final UserItem o) {
        return this.name.compareTo(o.name);
    }
}
