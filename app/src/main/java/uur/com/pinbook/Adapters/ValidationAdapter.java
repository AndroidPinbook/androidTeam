package uur.com.pinbook.Adapters;

import android.text.TextUtils;

/**
 * Created by mac on 9.12.2017.
 */

public class ValidationAdapter {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password){

        if(password.length() < 6)
            return false;
        else
            return true;
    }
}
