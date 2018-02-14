package uur.com.pinbook.Controller;

import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;


public class ClearSingletonClasses {

    public static void clearAllClasses(){
        FirebaseGetAccountHolder.setInstance(null);
        FirebaseGetFriends.setInstance(null);
        FirebaseGetGroups.setInstance(null);
    }
}
