package uur.com.pinbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 21.11.2017.
 */

public class EnterPageDataModel {

    private int imageID;
    String title;

    public EnterPageDataModel(int imageID, String title){

        setImageID(imageID);
        setTitle(title);
    }


    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<EnterPageDataModel> getDataList(){

        List<EnterPageDataModel> itemList = new ArrayList<>();

        int[] imagesIDs = new int[]{
                R.drawable.batman_icon,
                R.drawable.wonder_woman_icon,
                R.drawable.location_icon
        };

        String[] titles = new String[]{
                "Batman",
                "Wonder Woman",
                "Location"
        };

        for(int i =0; i<imagesIDs.length; i++){

            itemList.add(new EnterPageDataModel(imagesIDs[i], titles[i]));
        }

        return itemList;
    }
}
