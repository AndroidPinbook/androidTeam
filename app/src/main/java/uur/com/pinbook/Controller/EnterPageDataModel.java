package uur.com.pinbook.Controller;

import java.util.ArrayList;
import java.util.List;

import uur.com.pinbook.R;

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
                R.drawable.around_world,
                R.drawable.enjoy,
                R.drawable.everest
        };

        String[] titles = new String[]{
                "Throw a pin all around the world",
                "Contact with your friends and enjoy",
                "Set up mysterious games"
        };

        for(int i =0; i<imagesIDs.length; i++){

            itemList.add(new EnterPageDataModel(imagesIDs[i], titles[i]));
        }

        return itemList;
    }
}
