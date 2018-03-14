package uur.com.pinbook.DefaultModels;

import java.util.ArrayList;

import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.JavaFiles.Friend;

/**
 * Created by mac on 17.01.2018.
 */

public class ContactFriendList {

    private static ContactFriendList instance = null;
    private static ArrayList<Friend> contactFriendList;

    public static ContactFriendList getInstance(){

        if(instance == null) {
            contactFriendList = new ArrayList<Friend>();
            instance = new ContactFriendList();
        }
        return instance;
    }

    public static void setInstance(ContactFriendList instance) {
        ContactFriendList.instance = instance;
    }

    public ArrayList<Friend> getContactFriendList() {
        return contactFriendList;
    }

    public void setContactFriendList(ArrayList<Friend> contactFriendList) {
        this.contactFriendList = contactFriendList;
    }

    public void addFriend(Friend friend){
        contactFriendList.add(friend);
    }

    public int getSize(){
        return contactFriendList.size();
    }

    public Friend getFriend(int index){
        return contactFriendList.get(index);
    }

    public void removeFriend(String userID){
        int index = 0;

        for(index = 0; index < contactFriendList.size(); index++){
            Friend friend = contactFriendList.get(index);
            if(friend.getUserID() == userID) {
                contactFriendList.remove(index);
                break;
            }
        }
    }

    public void clearFriendList(){
        if(contactFriendList.size() > 0) {
            contactFriendList.clear();
        }
    }
}
