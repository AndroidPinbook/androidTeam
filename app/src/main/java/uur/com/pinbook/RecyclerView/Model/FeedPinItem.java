package uur.com.pinbook.RecyclerView.Model;

import java.io.Serializable;

/**
 * Created by ASUS on 17.2.2018.
 */

public class FeedPinItem implements Serializable {

    private String itemTag;
    private String itemImageUrl;
    private String description;
    private String itemDetailUrl;


    public FeedPinItem() {
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemDetailUrl() {
        return itemDetailUrl;
    }

    public void setItemDetailUrl(String itemDetailUrl) {
        this.itemDetailUrl = itemDetailUrl;
    }

}
