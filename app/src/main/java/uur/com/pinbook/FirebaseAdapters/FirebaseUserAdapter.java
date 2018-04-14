package uur.com.pinbook.FirebaseAdapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import uur.com.pinbook.JavaFiles.User;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.FirebaseFunctionsConstant.addFirebasePhoneNum;
import static uur.com.pinbook.ConstantsModel.FirebaseFunctionsConstant.addFirebaseUser;
import static uur.com.pinbook.ConstantsModel.FirebaseFunctionsConstant.addUserErrorNotif;
import static uur.com.pinbook.ConstantsModel.UrlRequestConstant.*;

public class FirebaseUserAdapter{

    //public static DatabaseReference mDbref;
    //public static String userId;

    User user;
    Context context;

    public FirebaseUserAdapter(Context context, User user){
        this.user = user;
        this.context = context;
        saveUserToDB();
        savePhoneNumToDB();
    }

    public void saveUserToDB() {

        JSONObject jsonUser = null;

        try {
            JSONObject jsonUserDtl = new JSONObject();

            jsonUserDtl.put(email, user.getEmail());
            jsonUserDtl.put(gender, user.getGender());
            jsonUserDtl.put(userName, user.getUsername());
            jsonUserDtl.put(name, user.getName());
            jsonUserDtl.put(surname, user.getSurname());
            jsonUserDtl.put(mobilePhone, user.getPhoneNum().toString());
            jsonUserDtl.put(birthday, user.getBirthdate());
            jsonUserDtl.put(profilePictureUrl, user.getProfilePicSrc());
            jsonUserDtl.put(profilePicMiniUrl, user.getMiniProfPicUrl());
            jsonUserDtl.put(providerId, user.getProviderId());

            jsonUser = new JSONObject();
            jsonUser.put(user.getUserId(), jsonUserDtl);

            FirebaseFunctions.getInstance()
                    .getHttpsCallable(addFirebaseUser)
                    .call(jsonUser)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Info", "Function call failure:" + e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    Log.i("Info", "Function call is ok");
                }
            });

        }catch (Exception e){
            //Toast.makeText(context, "Teknik hata:" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //new SaveJsonUser().execute(jsonUser);
    }

    public void savePhoneNumToDB() {

        if (user.getPhoneNum().trim().isEmpty()) return;

        JSONObject phoneJson = null;

        try {
            JSONObject phoneDtl = new JSONObject();

            phoneDtl.put(fbUserId, user.getUserId());

            phoneJson = new JSONObject();
            phoneJson.put(user.getPhoneNum(), phoneDtl);

            FirebaseFunctions.getInstance()
                    .getHttpsCallable(addFirebasePhoneNum)
                    .call(phoneJson)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Info", "Function call failure:" + e.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    Log.i("Info", "Function call is ok");
                }
            });

        }catch (Exception e){
            //Toast.makeText(context, "Teknik hata:" + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //new SaveUserPhoneNum().execute(phoneJson);
    }

    /*public class SaveJsonUser extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... voids) {

            JSONObject jsonObject = voids[0];

            try {
                //Log.i("Info", "jsonObject.toString():" + jsonObject.toString());
                URL requestedUrl;
                HttpURLConnection urlConnection;
                requestedUrl = new URL(addFirebaseUserReq);

                urlConnection = (HttpsURLConnection) requestedUrl.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(1500);

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                urlConnection.connect();
                urlConnection.getContent();
                urlConnection.disconnect();

            } catch (Exception e) {
                //Toast.makeText(context, "Teknik hata:" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return null;
        }
    }*/

    /*public class SaveUserPhoneNum extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... voids) {

            JSONObject jsonObject = voids[0];

            try {
                //Log.i("Info", "jsonObject.toString():" + jsonObject.toString());
                URL requestedUrl;
                HttpURLConnection urlConnection;
                requestedUrl = new URL(addFirebasePhoneNumReq);

                urlConnection = (HttpsURLConnection) requestedUrl.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(1500);

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                urlConnection.connect();
                urlConnection.getContent();
                urlConnection.disconnect();

            } catch (Exception e) {
                //Toast.makeText(context, "Teknik hata:" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return null;
        }
    }*/

    /*public static void saveUserInfo(User user) {

        Map<String, String> values = new HashMap<>();

        userId = user.getUserId();
        mDbref = FirebaseDatabase.getInstance().getReference().child(Users);

        values.put(email, user.getEmail());
        values.put(gender, user.getGender());
        values.put(userName, user.getUsername());
        values.put(name, user.getName());
        values.put(surname, user.getSurname());
        values.put(mobilePhone, user.getPhoneNum().toString());
        values.put(birthday, user.getBirthdate());
        values.put(profilePictureUrl, user.getProfilePicSrc());
        values.put(profilePicMiniUrl, user.getMiniProfPicUrl());
        values.put(providerId, user.getProviderId());
        setValuesToCloud(values);

        savePhoneNumInfo(user.getPhoneNum());
    }*/

    /*private static void savePhoneNumInfo(String phoneNum) {

        if (phoneNum.trim().isEmpty()) return;

        mDbref = FirebaseDatabase.getInstance().getReference().child(PhoneNums);
        Map<String, Object> values = new HashMap<>();
        values.put(fbUserId, userId);
        mDbref.child(phoneNum).setValue(values);
    }

    public static void setValuesToCloud(Map<String, String> values) {

        mDbref.child(userId).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }*/
}
