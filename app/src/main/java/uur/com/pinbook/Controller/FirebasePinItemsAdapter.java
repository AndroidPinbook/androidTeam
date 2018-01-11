package uur.com.pinbook.Controller;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import uur.com.pinbook.JavaFiles.LocationDb;
import uur.com.pinbook.JavaFiles.PinData;

import static uur.com.pinbook.JavaFiles.ConstValues.*;

public class FirebasePinItemsAdapter extends AppCompatActivity{

    private StorageReference riversRef;
    private StorageReference mStorageRef;

    private DatabaseReference mDbref;
    public static LocationDb tempLocationDb = null;
    public static PinData tempPinData = null;

    private Uri downloadImageUri;
    private Uri downloadTextUri;
    private Uri downloadVideoUri;

    public void savePinItems(LocationDb LocationDb, PinData pinData){

        riversRef = null;
        mDbref = null;

        tempLocationDb = LocationDb;
        tempPinData = pinData;

        mDbref = FirebaseDatabase.getInstance().getReference().child(PinItems).child(LocationDb.getUserId())
                .child(LocationDb.getLocationId());

        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Video datasini atalim
        if(pinData.getPinVideoUri() != null){
            riversRef = mStorageRef.child(PinItems).child(LocationDb.getUserId())
                    .child(LocationDb.getLocationId()).child(video).child(pinVideoImage + ".mp4");

            riversRef.putFile(pinData.getPinVideoUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            downloadVideoUri = taskSnapshot.getDownloadUrl();

                            if(downloadVideoUri != null) {
                                addPinItem(videoURL, downloadVideoUri.toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Log.i("Info", "put file err:" + exception.toString());
                        }
                    });
        }

        //Text datasini atalim
        if(pinData.getPinTextUri() != null){
            riversRef = mStorageRef.child(PinItems).child(LocationDb.getUserId())
                    .child(LocationDb.getLocationId()).child(text).child(pinTextImage + ".jpg");

            riversRef.putFile(pinData.getPinTextUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            downloadTextUri = taskSnapshot.getDownloadUrl();

                            if(downloadTextUri != null) {
                                addPinItem(textURL, downloadTextUri.toString());
                            }

                            if(!tempPinData.getNoteText().equals(" ")){
                                addPinItem(text, tempPinData.getNoteText());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Log.i("Info", "put file err:" + exception.toString());
                        }
                    });
        }

        //Image datasini atalim
        if(pinData.getPinImageUri() != null){
            riversRef = mStorageRef.child(PinItems).child(LocationDb.getUserId())
                    .child(LocationDb.getLocationId()).child(picture).child(pinPictureImage + ".jpg");

            riversRef.putFile(pinData.getPinImageUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            downloadImageUri = taskSnapshot.getDownloadUrl();

                            if(downloadImageUri != null) {
                                addPinItem(pictureURL, downloadImageUri.toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Log.i("Info", "put file err:" + exception.toString());
                        }
                    });
        }
    }

    private void addPinItem(String itemName, String value) {

            mDbref.child(itemName).setValue(value, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.i("Info", "     >>databaseError:" + databaseError);
                    }
                });
    }
}
