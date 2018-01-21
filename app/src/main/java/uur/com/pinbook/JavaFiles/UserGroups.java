package uur.com.pinbook.JavaFiles;

import java.util.ArrayList;

/**
 * Created by mac on 19.01.2018.
 */

public class UserGroups {

    String userID;

    ArrayList<Group> groupList;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }
}
