package uur.com.pinbook.RecyclerView.Model;

import java.util.ArrayList;

/**
 * Created by ASUS on 17.2.2018.
 */

public class FeedAllItem {

    private String ownerId;
    private String ownerPictureUrl;
    private String ownerName;
    private String locationId;
    private int time;

    private ArrayList<FeedPinItem> feedPinItems;

    public FeedAllItem() {
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public ArrayList<FeedPinItem> getFeedPinItems() {
        return feedPinItems;
    }

    public void setFeedPinItems(ArrayList<FeedPinItem> feedPinItems) {
        this.feedPinItems = feedPinItems;
    }

    public void setOwnerPictureUrl(String ownerPictureUrl) {
        this.ownerPictureUrl = ownerPictureUrl;
    }

    public String getOwnerPictureUrl() {
        return ownerPictureUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int timeStamp) {
        this.time = timeStamp;
    }
}
