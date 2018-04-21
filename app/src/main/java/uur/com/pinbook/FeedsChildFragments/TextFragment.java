package uur.com.pinbook.FeedsChildFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Model.FeedAllItem;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;


public class TextFragment extends Fragment {

    @BindView(R.id.feedTextText)
    TextView feedItemText;
    FeedAllItem feedAllItem;
    FeedPinItem feedPinItem;
    int clickedItem;;

    public TextFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_item_text, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle=getArguments();
        feedAllItem = (FeedAllItem) bundle.getSerializable("feedAllItem");
        clickedItem = (Integer) bundle.getSerializable("clickedItem");

        feedPinItem = feedAllItem.getFeedPinItems().get(clickedItem);

        Toast.makeText(getContext(), feedPinItem.getItemTag(), Toast.LENGTH_SHORT).show();
        Log.i("neredeyiz--->", "TextFragment");

        //feed text
        if(feedPinItem.getDescription() != null){
            feedItemText.setText(feedPinItem.getDescription());
        }else{
            String s = "Text boş..";
            feedItemText.setText(s);
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //((FeedDetailActivity) getActivity()).disableNavigationIcon();
        //((FeedDetailActivity) getActivity()).setToolbarTitle(12);
    }
}
