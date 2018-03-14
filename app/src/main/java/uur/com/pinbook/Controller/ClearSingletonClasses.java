package uur.com.pinbook.Controller;

import uur.com.pinbook.DefaultModels.ContactFriendList;
import uur.com.pinbook.DefaultModels.FacebookFriendList;
import uur.com.pinbook.DefaultModels.GetContactList;
import uur.com.pinbook.FirebaseGetData.FBGetInviteInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.FirebaseGetData.FirebaseGetFriends;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;


public class ClearSingletonClasses {

    public static void clearAllClasses(){
        FirebaseGetAccountHolder.setInstance(null);
        FirebaseGetFriends.setInstance(null);
        FirebaseGetGroups.setInstance(null);
        FBGetInviteInbounds.setInstance(null);
        FBGetInviteOutbounds.setInstance(null);
        GetContactList.setInstance(null);
        FacebookFriendList.setInstance(null);
        ContactFriendList.setInstance(null);
    }
}
