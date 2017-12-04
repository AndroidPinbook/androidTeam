package uur.com.pinbook.JavaFiles;

import java.io.Serializable;

/**
 * Created by mac on 2.12.2017.
 */

public class User implements Serializable{

    String userId;
    String email;
    String username;
    String name;
    String surname;
    String gender;
    String profilePicSrc;
    String birthdate;
    String phoneNum;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicSrc() {
        return profilePicSrc;
    }

    public void setProfilePicSrc(String profilePicSrc) {
        this.profilePicSrc = profilePicSrc;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
