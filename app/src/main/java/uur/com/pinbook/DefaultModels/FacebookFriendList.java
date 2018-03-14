package uur.com.pinbook.DefaultModels;

import java.util.ArrayList;

import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;

/**
 * Created by mac on 17.01.2018.
 */

public class FacebookFriendList {

    private static FacebookFriendList instance = null;
    private static ArrayList<Friend> facebookFriendList;

    public static FacebookFriendList getInstance(){

        if(instance == null) {
            facebookFriendList = new ArrayList<Friend>();
            instance = new FacebookFriendList();
        }
        return instance;
    }

    public static void setInstance(FacebookFriendList instance) {
        FacebookFriendList.instance = instance;
    }

    public ArrayList<Friend> getFacebookFriendList() {
        return facebookFriendList;
    }

    public void setSelectedFriendList(ArrayList<Friend> facebookFriendList) {
        this.facebookFriendList = facebookFriendList;
    }

    public void addFriend(Friend friend){
        facebookFriendList.add(friend);
    }

    public int getSize(){
        return facebookFriendList.size();
    }

    public Friend getFriend(int index){
        return facebookFriendList.get(index);
    }

    public void removeFriend(String userID){
        int index = 0;

        for(index = 0; index < facebookFriendList.size(); index++){
            Friend friend = facebookFriendList.get(index);
            if(friend.getUserID() == userID) {
                facebookFriendList.remove(index);
                break;
            }
        }
    }

    public void clearFriendList(){
        if(facebookFriendList.size() > 0) {
            facebookFriendList.clear();
        }
    }
}
