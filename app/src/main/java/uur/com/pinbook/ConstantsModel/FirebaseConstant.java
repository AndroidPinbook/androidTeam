package uur.com.pinbook.ConstantsModel;

/**
 * Created by mac on 16.01.2018.
 */

public class FirebaseConstant {

    public static final String GEO_FIRE_DB = "https://androidteam-f4c25.firebaseio.com";
    public static final String GEO_FIRE_DB_ITEMS = "https://androidteam-f4c25.firebaseio.com/PinItems";
    public static final String GEO_FIRE_DB_LOCATIONS = "https://androidteam-f4c25.firebaseio.com/Locations";
    public static final String GEO_FIRE_DB_USER_LOCATIONS = "https://androidteam-f4c25.firebaseio.com/UserLocations";
    public static final String GEO_FIRE_DB_REG_BASED_LOCATION = "https://androidteam-f4c25.firebaseio.com/RegionBasedLocation";
    public static final String GEO_FIRE_DB_PIN_MODELS = "https://androidteam-f4c25.firebaseio.com/PinModels";
    public static final String GEO_FIRE_DB_GROUPS = "https://androidteam-f4c25.firebaseio.com/Groups";
    public static final String GEO_FIRE_DB_USERGROUPS = "https://androidteam-f4c25.firebaseio.com/UserGroups";
    public static final String FB_APPLICATION_ID = "1:695005347563:android:e77f50983708f0c0";

    //Firebase
    public static final String PinItems = "PinItems";
    public static final String UserLocations = "UserLocations";
    public static final String Users = "Users";
    public static final String Locations = "Locations";
    public static final String PinModels = "PinModels";
    public static final String Friends = "Friends";
    public static final String Groups = "Groups";
    public static final String UserGroups = "UserGroups";
    public static final String Feeds = "Feeds";
    public static final String FacebookUsers = "FacebookUsers";
    public static final String InviteOutbound = "InviteOutbound";
    public static final String InviteInbound = "InviteInbound";
    public static final String PhoneNums = "PhoneNums";

    //Firebase Group constants
    public static final String Admin = "Admin";
    public static final String GroupName = "GroupName";
    public static final String GroupPictureUrl = "GroupPictureUrl";
    public static final String UserList = "UserList";
    public static final String GroupImage = "GroupImage";
    public static final String GroupID = "GroupID";


    //Firebase users childs
    public static final String userID = "userID";
    public static final String userName = "userName";
    public static final String nameSurname = "nameSurname";
    public static final String name = "name";
    public static final String surname = "surname";
    public static final String gender = "gender";
    public static final String birthday = "birthday";
    public static final String email = "email";
    public static final String profilePictureUrl = "profilePictureUrl";
    public static final String profilePicMiniUrl = "profilePicMiniUrl";
    public static final String password = "password";
    public static final String mobilePhone = "mobilePhone";
    public static final String provider = "provider";
    public static final String providerId = "providerId";
    public static final String profilePictureId = "profilePictureId";

    //Firebase location childs
    public static final String location = "location";
    public static final String countryCode = "countryCode";
    public static final String countryName = "countryName";
    public static final String timestamp = "timestamp";
    public static final String postalCode = "postalCode";
    public static final String thorough = "thorough";
    public static final String subThorough = "subThorough";
    public static final String geolocation = "geolocation";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String city = "city";

    //Firebase pinItems child
    public static final String pictureId = "pictureId";
    public static final String pictureURL = "pictureURL";
    public static final String videoId = "videoId";
    public static final String videoURL = "videoURL";
    public static final String textId = "textId";
    public static final String textURL = "textURL";
    public static final String text = "text";
    public static final String video = "video";
    public static final String picture = "picture";
    public static final String videoImageURL = "videoImageURL";


    public static final String pinVideo = "pinVideo";
    public static final String pinPictureImage = "pinPictureImage";
    public static final String pinVideoImage = "pinVideoImage";
    public static final String pinTextImage = "pinTextImage";
    public static final String ProfileImages = "ProfileImages";

    //Firebase pinModel child
    public static final String notified = "Notified";
    public static final String owner = "Owner";
    //public static final String pinTypeInfo = "PinTypeInfo";
    public static final String property = "Property";
    public static final String toWhom = "ToWhom";

    //Firebase FacebookUSers childs
    public static final String fbUserId = "fbUserId";

    //Pin properties
    public static final String propFriends = "Friends";
    public static final String propPersons = "Persons";
    public static final String propOnlyMe = "OnlyMe";
    public static final String propGroups = "Groups";

    //Pin toWhom properties
    public static final String toWhomAll = "ALL";
    public static final String toWhomSpecial = "SPECIAL";

    //Pin notify Flags
    public static final String notifyYes = "Y";
    public static final String notifyNo  = "N";

    //Inbound-Outbound invite constants
    public static final String Yes  = "Y";
    public static final String No  = "N";
    public static final String OutbndWaiting  = "W";
    public static final String InbndWaiting  = "I";

    //Dynamic Link constants
    public static final String dynamicLinkDomain = "g2wx4.app.goo.gl";
    public static final String appShareLink = "https://play.google.com/store/apps/details?id=com.supercell.clashofclans";
    public static final String appShareTitle = "Share This App";


}
