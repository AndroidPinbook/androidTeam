package uur.com.pinbook.DefaultModels;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.ConstantsModel.NumericConstant.*;

public class GetContactList extends Activity{

    private static GetContactList contactInstance = null;

    private ArrayList<User> contactList = new ArrayList<>();

    private Context context = null;

    public synchronized static GetContactList getInstance(Context context){

        if(contactInstance == null)
            contactInstance = new GetContactList(context);

        return contactInstance;
    }

    public ArrayList<User> getContactList() {
        return contactList;
    }

    public GetContactList(Context context){
        this.context = context;
        checkPermissionForContact();
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException("Klonlama yapılamaz!!!");
    }

    public static void setInstance(GetContactList instance) {
        contactInstance = instance;
    }

    public void checkPermissionForContact() {

        Activity activity = (Activity) context;

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            getPhoneList();
        }
    }

    public void getPhoneList(){
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            User user = new User();
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
            user.setName(name);

            String clearPhoneNum ="";

            for (int i = 0; i < phoneNumber.length(); i++) {

                char ch = phoneNumber.charAt(i);
                if (Character.isDigit(ch)) {
                    clearPhoneNum += ch;
                }
            }

            user.setPhoneNum(clearPhoneNum);

            Log.i("Info", "name         :" + name);
            Log.i("Info", "phoneNumber  :" + phoneNumber);
            Log.i("Info", "clearPhoneNum:" + clearPhoneNum);
            contactList.add(user);

        }
        phones.close();
    }
}
