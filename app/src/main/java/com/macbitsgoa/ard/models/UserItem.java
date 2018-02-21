package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Model class 'UserItem'.
 * This data is stored in firebase directory {@link AHC#FDR_USERS} with uid
 * (obtained by {@link FirebaseUser#getUid()}) as key.
 * A user has read access to data of all users, and write access to his/her info.
 *
 * @author Rushikesh Jogdand
 */
@SuppressWarnings("WeakerAccess")
public class UserItem extends RealmObject {

    @PrimaryKey
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

    /**
     * Is admin or not?
     */
    private boolean admin;

    public UserItem() {
        this.uid = "";
        email = "";
        photoUrl = "";
        name = "";
        desc = "";
        admin = false;
    }

    public UserItem(@NonNull String uid, @Nullable String name, @Nullable String email,
                    @Nullable String photoUrl, @Nullable String desc, boolean admin) {
        this.uid = uid;
        setName(name);
        setEmail(email);
        setPhotoUrl(photoUrl);
        setDesc(desc);
        setAdmin(admin);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? "" : uid;
    }

    @NonNull
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
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
    public boolean equals(Object obj) {
        return obj instanceof UserItem && ((UserItem) obj).getUid().equals(this.uid);
    }

    @Override
    public String toString() {
        return "UserItem{"
                + "uid='" + uid + '\''
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", photoUrl='" + photoUrl + '\''
                + ", desc='" + desc + '\''
                + ", admin=" + admin
                + '}';
    }
}
