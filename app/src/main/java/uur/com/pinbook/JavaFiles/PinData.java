package uur.com.pinbook.JavaFiles;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mac on 28.12.2017.
 */

public class PinData {


    Uri pinImageUri;
    Uri pinTextUri;
    Uri pinVideoUri;
    Uri pinVideoImageUri;
    String noteText;

    String videoRealPath;
    String imageRealPath;

    public Uri getPinVideoImageUri() {
        return pinVideoImageUri;
    }

    public void setPinVideoImageUri(Uri pinVideoImageUri) {
        this.pinVideoImageUri = pinVideoImageUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        this.imageRealPath = imageRealPath;
    }

    public Uri getPinImageUri() {
        return pinImageUri;
    }

    public void setPinImageUri(Uri pinImageUri) {
        this.pinImageUri = pinImageUri;
    }

    public Uri getPinTextUri() {
        return pinTextUri;
    }

    public void setPinTextUri(Uri pinTextUri) {
        this.pinTextUri = pinTextUri;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Uri getPinVideoUri() {
        return pinVideoUri;
    }

    public void setPinVideoUri(Uri pinVideoUri) {
        this.pinVideoUri = pinVideoUri;
    }

    public String getVideoRealPath() {
        return videoRealPath;
    }

    public void setVideoRealPath(String videoRealPath) {
        this.videoRealPath = videoRealPath;
    }
}