package uur.com.pinbook.JavaFiles;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by ASUS on 17.2.2018.
 */

public class Feed {

    Uri pinImageUri;
    Uri pinTextUri;
    Uri pinVideoUri;
    Uri pinVideoImageUri;
    String noteText;
    String locationID;
    String notifiedFlag;
    String owner;
    String property;
    String toWhom;

    String videoRealPath;
    String imageRealPath;

    ArrayList<Friend> friendList;
    ArrayList<Group> groupList;

    public Feed() {
    }

    public Uri getPinImageUri() {
        return pinImageUri;
    }

    public void setPinImageUri(Uri pinImageUri) {
        this.pinImageUri = pinImageUri;
    }

    public Uri getPinTextUri() {
        return pinTextUri;
    }

    public void setPinTextUri(Uri pinTextUri) {
        this.pinTextUri = pinTextUri;
    }

    public Uri getPinVideoUri() {
        return pinVideoUri;
    }

    public void setPinVideoUri(Uri pinVideoUri) {
        this.pinVideoUri = pinVideoUri;
    }

    public Uri getPinVideoImageUri() {
        return pinVideoImageUri;
    }

    public void setPinVideoImageUri(Uri pinVideoImageUri) {
        this.pinVideoImageUri = pinVideoImageUri;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getNotifiedFlag() {
        return notifiedFlag;
    }

    public void setNotifiedFlag(String notifiedFlag) {
        this.notifiedFlag = notifiedFlag;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getToWhom() {
        return toWhom;
    }

    public void setToWhom(String toWhom) {
        this.toWhom = toWhom;
    }

    public String getVideoRealPath() {
        return videoRealPath;
    }

    public void setVideoRealPath(String videoRealPath) {
        this.videoRealPath = videoRealPath;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        this.imageRealPath = imageRealPath;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    public ArrayList<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }
}
