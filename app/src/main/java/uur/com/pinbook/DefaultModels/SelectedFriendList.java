package uur.com.pinbook.DefaultModels;

import java.util.ArrayList;

import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;

/**
 * Created by mac on 17.01.2018.
 */

public class SelectedFriendList {

    private static SelectedFriendList instance = null;
    private static ArrayList<Friend> selectedFriendList;

    public static SelectedFriendList getInstance(){

        if(instance == null) {
            selectedFriendList = new ArrayList<Friend>();
            instance = new SelectedFriendList();
        }
        return instance;
    }

    public ArrayList<Friend> getSelectedFriendList() {
        return selectedFriendList;
    }

    public void setSelectedFriendList(ArrayList<Friend> selectedFriendList) {
        this.selectedFriendList = selectedFriendList;
    }

    public void addFriend(Friend friend){
        selectedFriendList.add(friend);
    }

    public int getSize(){
        return selectedFriendList.size();
    }

    public Friend getFriend(int index){
        return selectedFriendList.get(index);
    }

    public void removeFriend(String userID){
        int index = 0;

        for(index = 0; index < selectedFriendList.size(); index++){
            Friend friend = selectedFriendList.get(index);
            if(friend.getUserID() == userID) {
                selectedFriendList.remove(index);
                break;
            }
        }
    }

    public void clearFriendList(){
        selectedFriendList.clear();
    }
}
