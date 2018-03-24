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
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;


public class TextFragment extends Fragment {

    @BindView(R.id.feedTextText)
    TextView feedItemText;

    FeedPinItem singeItem;

    public TextFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_item_text, container, false);

        ButterKnife.bind(this, view);

        Bundle bundle=getArguments();
        FeedPinItem f = (FeedPinItem) bundle.getSerializable("singleItem");

        Toast.makeText(getContext(), f.getItemTag(), Toast.LENGTH_SHORT).show();
        Log.i("neredeyiz--->", "TextFragment");

        //feed text
        if(f.getDescription() != null){
            feedItemText.setText(f.getDescription());
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
