package uur.com.pinbook.FeedsChildFragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;

public class VideoFragment extends Fragment {

    @BindView(R.id.videoView)
    VideoView feedVideoView;


    //////////////////////////////////////////////////////

    //VideoView videoView;
    int position = 0;
    MediaController mediaController;

    /////////////////////////////////////////////////////

    public VideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_item_video, container, false);

        ButterKnife.bind(this, view);

        //exoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.feedVideo);

        Bundle bundle = getArguments();
        FeedPinItem f = (FeedPinItem) bundle.getSerializable("singleItem");

        Toast.makeText(getContext(), f.getItemTag(), Toast.LENGTH_SHORT).show();
        Log.i("neredeyiz--->", "VideoFragment");
        Log.i("video detay Url", f.getItemDetailUrl());

        Uri videoURI = Uri.parse(f.getItemDetailUrl());

        feedVideoView = (VideoView) view.findViewById(R.id.videoView);
        mediaController = new MediaController(getContext());
        mediaController.setAnchorView(feedVideoView);

        feedVideoView.setMediaController(mediaController);
        feedVideoView.setVideoURI(videoURI);
        //feedVideoView.start();

        // When the video file ready for playback.
        feedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {


                feedVideoView.seekTo(position);
                if (position == 0) {
                    feedVideoView.start();
                }

            }
        });




/*
        feedVideoView.setVideoURI(uri);
        feedVideoView.requestFocus();
        feedVideoView.start();
*/

/*
        try

        {


            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);


            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }

*/




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //((FeedDetailActivity) getActivity()).disableNavigationIcon();
        //((FeedDetailActivity) getActivity()).setToolbarTitle(12);
    }



}
