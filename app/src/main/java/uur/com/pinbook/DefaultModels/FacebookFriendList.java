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
            facebookFriendList = new ArrayList<>();
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

    public void addFriend(Friend friend){
        facebookFriendList.add(friend);
    }

    public int getSize(){
        return facebookFriendList.size();
    }
}
