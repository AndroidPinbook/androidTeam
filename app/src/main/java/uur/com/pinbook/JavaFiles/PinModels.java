package uur.com.pinbook.JavaFiles;

import java.util.ArrayList;

/**
 * Created by mac on 11.01.2018.
 */

public class PinModels {

    String locationID;
    String notifiedFlag;
    String owner;
    String property;
    String toWhom;

    ArrayList<Friend> friendList;
    ArrayList<Group> groupList;

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
