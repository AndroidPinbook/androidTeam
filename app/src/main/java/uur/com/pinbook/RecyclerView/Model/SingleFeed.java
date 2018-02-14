package uur.com.pinbook.RecyclerView.Model;

import java.util.ArrayList;

/**
 * Created by pratap.kesaboyina on 30-11-2015.
 */
public class SingleFeed {

    private String image;
    private String title;
    private String name;
    private String nameImage;
    private int count;
    private String type;

    private ArrayList<FeedInnerItem> allItemsInSingleFeed;

    public SingleFeed() {

    }
    public SingleFeed(String headerTitle, ArrayList<FeedInnerItem> allItemsInSection) {
        this.allItemsInSingleFeed = allItemsInSection;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameImage() {
        return nameImage;
    }

    public void setNameImage(String nameImage) {
        this.nameImage = nameImage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<FeedInnerItem> getAllItemsInSingleFeed() {
        return allItemsInSingleFeed;
    }

    public void setAllItemsInSingleFeed(ArrayList<FeedInnerItem> allItemsInSingleFeed) {
        this.allItemsInSingleFeed = allItemsInSingleFeed;
    }

}
