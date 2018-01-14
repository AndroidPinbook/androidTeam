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

    ArrayList<String> usersList;
    ArrayList<String> groupList;

    public void initializePinModel(){

        this.locationID = null;
        notifiedFlag = null;
        owner = null;
        property = null;
        toWhom = null;

        if(usersList != null)
            usersList.clear();

        if(groupList != null)
            groupList.clear();
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

    public ArrayList<String> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<String> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }
}
