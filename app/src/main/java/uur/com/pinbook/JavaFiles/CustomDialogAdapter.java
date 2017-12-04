package uur.com.pinbook.JavaFiles;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import uur.com.pinbook.R;

public class CustomDialogAdapter extends DialogFragment {

    public static void showErrorDialog(Context context, String errMessage){

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("OPPS!");
        builder.setIcon(R.drawable.toast_error_icon);
        builder.setMessage(errMessage);

        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showToastMessage(Context context, View view){

        //LayoutInflater inflater = getLayoutInflater();
        //View view = inflater.inflate(R.layout.cust_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        toast.setView(view);
        toast.show();
    }
}
