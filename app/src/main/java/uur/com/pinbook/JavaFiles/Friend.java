package uur.com.pinbook.JavaFiles;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by mac on 13.01.2018.
 */

public class Friend implements Serializable{

    String userID;
    String nameSurname;
    String profilePicSrc;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getProfilePicSrc() {
        return profilePicSrc;
    }

    public void setProfilePicSrc(String profilePicSrc) {
        this.profilePicSrc = profilePicSrc;
    }
}
