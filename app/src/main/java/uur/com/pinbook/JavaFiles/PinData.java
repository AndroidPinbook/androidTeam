package uur.com.pinbook.JavaFiles;

/**
 * Created by ASUS on 23.12.2017.
 */

public class PinData {

    String userId;
    String locationId;
    String pictureOnPin;
    String videoOnPin;
    String noteOnPin;

    public String getUserId() {
        return userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getPictureOnPin() {
        return pictureOnPin;
    }

    public String getVideoOnPin() {
        return videoOnPin;
    }

    public String getNoteOnPin() {
        return noteOnPin;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setPictureOnPin(String pictureOnPin) {
        this.pictureOnPin = pictureOnPin;
    }

    public void setVideoOnPin(String videoOnPin) {
        this.videoOnPin = videoOnPin;
    }

    public void setNoteOnPin(String noteOnPin) {
        this.noteOnPin = noteOnPin;
    }
}

