package com.macbitsgoa.ard.models;

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
        desc = "";
    }

    public UserItem(String uid, String name, String email, String photoUrl, String desc, boolean admin) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.desc = desc;
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "name=" + getName() + AHC.SEPARATOR + "email=" + getEmail() + AHC.SEPARATOR + "photoUrl="
                + getPhotoUrl();
    }
}
