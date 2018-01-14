package uur.com.pinbook.JavaFiles;

import android.location.Location;

/**
 * Created by mac on 29.12.2017.
 */

public class UserLocation {

    String userId;
    String countryCode;
    String countryName;
    String locTimestamp;
    String postalCode;
    String thoroughFare;
    String subThoroughfare;
    String latitude;
    String longitude;
    Location location;
    String locationId;
    String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getThoroughFare() {
        return thoroughFare;
    }

    public void setThoroughFare(String thoroughFare) {
        this.thoroughFare = thoroughFare;
    }

    public String getSubThoroughfare() {
        return subThoroughfare;
    }

    public void setSubThoroughfare(String subThoroughfare) {
        this.subThoroughfare = subThoroughfare;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLocTimestamp() {
        return locTimestamp;
    }

    public void setLocTimestamp(String locTimestamp) {
        this.locTimestamp = locTimestamp;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getThorough() {
        return thoroughFare;
    }

    public void setThorough(String thoroughFare) {
        this.thoroughFare = thoroughFare;
    }

    public String getSubThorough() {
        return subThoroughfare;
    }

    public void setSubThorough(String subThoroughfare) {
        this.subThoroughfare = subThoroughfare;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
