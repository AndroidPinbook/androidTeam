package uur.com.pinbook.Activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import cn.refactor.lib.colordialog.PromptDialog;
import uur.com.pinbook.R;

public class PlayVideoActivity extends AppCompatActivity {

    private String videoUriText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                videoUriText= null;
            } else {
                videoUriText= extras.getString("videoUri");
            }
        } else {
            videoUriText= (String) savedInstanceState.getSerializable("videoUri");
        }

        playVideo();
    }

    private void playVideo() {

        if(videoUriText == null) {
            new PromptDialog(this)
                    .setDialogType(PromptDialog.DIALOG_TYPE_DEFAULT)
                    .setAnimationEnable(true)
                    .setTitleText("HATA")
                    .setContentText("Video oynatilamiyor")
                    .setPositiveListener("Tamam", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                            finish();
                            onBackPressed();
                        }
                    }).show();
        }

        Uri videoUri = Uri.parse(videoUriText);

        VideoView pinVideoView = (VideoView) findViewById(R.id.pinVideoView);

        pinVideoView = (VideoView) findViewById(R.id.pinVideoView);
        pinVideoView.setVisibility(View.VISIBLE);

        MediaController mediacontroller = new MediaController(PlayVideoActivity.this);

        mediacontroller.setAnchorView(pinVideoView);

        pinVideoView.setMediaController(mediacontroller);




        DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) pinVideoView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        pinVideoView.setLayoutParams(params);





        //ViewGroup.LayoutParams params = pinVideoView.getLayoutParams();
        //params.height = 150;
        //pinVideoView.setLayoutParams(params);

        pinVideoView.setVideoURI(videoUri);
        pinVideoView.requestFocus();

        pinVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }
}
