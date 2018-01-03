package uur.com.pinbook.Controller;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import uur.com.pinbook.Activities.LoginPageActivity;
import uur.com.pinbook.Activities.ProfilePhotoActivity;
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


    public static void showDialogSuccess(Context context, String errMessage){

        new PromptDialog(context)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText("Başarılı")
                .setContentText(errMessage)
                .setPositiveListener("ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showDialogError(Context context, String errMessage){

        new PromptDialog(context)
                .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setAnimationEnable(true)
                .setTitleText("HATA")
                .setContentText(errMessage)
                .setPositiveListener("Tamam", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showDialogInfo(Context context, String errMessage){

        new PromptDialog(context)
                .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                .setAnimationEnable(true)
                .setTitleText("Info")
                .setContentText(errMessage)
                .setPositiveListener("Tamam", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showDialogWarning(Context context, String errMessage){

        new PromptDialog(context)
                .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                .setAnimationEnable(true)
                .setTitleText("WARNING")
                .setContentText(errMessage)
                .setPositiveListener("Tamam", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showCustomDialog(final Context context, String errMessage){
        ColorDialog dialog = new ColorDialog(context);
        dialog.setColor(R.color.background);
        dialog.setAnimationEnable(true);
        dialog.setTitle("operation");
        dialog.setContentText("content_text");
        dialog.setPositiveListener("text_know", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(context, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    public static void showCustomDialog2(final Context context, String errMessage){
        ColorDialog dialog = new ColorDialog(context);
        dialog.setTitle("operation");
        dialog.setAnimationEnable(true);
        dialog.setAnimationIn(getInAnimationTest(context));
        dialog.setAnimationOut(getOutAnimationTest(context));
        dialog.setContentText("Email sıfırla");
        dialog.addContentView(null, null);
        dialog.setContentImage((R.mipmap.ic_help));
        dialog.setPositiveListener("delete", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(context, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
            }
        })
                .setNegativeListener("cancel", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        Toast.makeText(context, dialog.getNegativeText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    public static AnimationSet getInAnimationTest(Context context) {
        AnimationSet out = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(150);
        ScaleAnimation scale = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        out.addAnimation(alpha);
        out.addAnimation(scale);
        return out;
    }

    public static AnimationSet getOutAnimationTest(Context context) {
        AnimationSet out = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(150);
        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        out.addAnimation(alpha);
        out.addAnimation(scale);
        return out;
    }




}
