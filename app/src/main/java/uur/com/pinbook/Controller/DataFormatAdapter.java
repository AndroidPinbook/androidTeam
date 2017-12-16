package uur.com.pinbook.Controller;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by mac on 9.12.2017.
 */

public class DataFormatAdapter {

    public String formatE164Number(String countryCode, String phNum) {

        String e164Number;

        if (TextUtils.isEmpty(countryCode)) {
            e164Number = phNum;
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                e164Number = PhoneNumberUtils.formatNumberToE164(phNum, countryCode);
            } else {
                try {
                    PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber phoneNumber = instance.parse(phNum, countryCode);
                    e164Number = instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

                } catch (NumberParseException e) {
                    Log.i("Info" ," Phone error"+ e.getMessage());
                    e164Number = phNum;
                }
            }
        }

        return e164Number;
    }
}
