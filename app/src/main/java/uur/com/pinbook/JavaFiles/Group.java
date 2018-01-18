package uur.com.pinbook.JavaFiles;

import java.util.ArrayList;

import uur.com.pinbook.ConstantsModel.StringConstant;

/**
 * Created by mac on 11.01.2018.
 */

public class Group {

    String groupID;
    String adminID;
    String pictureUrl;
    String groupName;

    ArrayList<String> userList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }
}
