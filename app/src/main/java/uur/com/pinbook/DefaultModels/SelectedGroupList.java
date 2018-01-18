package uur.com.pinbook.DefaultModels;

import java.util.ArrayList;

import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.JavaFiles.Group;

/**
 * Created by mac on 17.01.2018.
 */

public class SelectedGroupList {

    private static SelectedGroupList instance = null;
    private static ArrayList<Group> selectedGroupList;

    public static SelectedGroupList getInstance(){

        if(instance == null) {
            selectedGroupList = new ArrayList<Group>();
            instance = new SelectedGroupList();
        }
        return instance;
    }

    public ArrayList<Group> getGroupList() {
        return selectedGroupList;
    }

    public void setGroupList(ArrayList<Group> groupList) {
        SelectedGroupList.selectedGroupList = groupList;
    }

    public void addGroup(Group group){
        selectedGroupList.add(group);
    }

    public int getSize(){
        return selectedGroupList.size();
    }

    public Group getGroup(int index){
        return selectedGroupList.get(index);
    }

    public void clearGrouplist(){
        selectedGroupList.clear();
    }

    public void removeGroup(String groupID){
        int index = 0;

        for(index = 0; index < selectedGroupList.size(); index++){
            Group group = selectedGroupList.get(index);
            if(group.getGroupID() == groupID) {
                selectedGroupList.remove(index);
                break;
            }
        }
    }
}
