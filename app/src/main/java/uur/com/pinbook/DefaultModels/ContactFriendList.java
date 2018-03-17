package uur.com.pinbook.DefaultModels;

import java.util.ArrayList;
import uur.com.pinbook.JavaFiles.Friend;

public class ContactFriendList {

    private static ContactFriendList contactInstance = null;
    private static ArrayList<Friend> contactFriendList;

    public static ContactFriendList getInstance(){

        if(contactInstance == null) {
            contactFriendList = new ArrayList<Friend>();
            contactInstance = new ContactFriendList();
        }
        return contactInstance;
    }

    public static void setInstance(ContactFriendList instance) {
        contactInstance = instance;
    }

    public ArrayList<Friend> getContactFriendList() {
        return contactFriendList;
    }

    public void addFriend(Friend friend){
        contactFriendList.add(friend);
    }

    public int getSize(){
        return contactFriendList.size();
    }
}
