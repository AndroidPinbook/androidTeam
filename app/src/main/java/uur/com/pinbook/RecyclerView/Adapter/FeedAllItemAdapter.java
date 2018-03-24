package uur.com.pinbook.RecyclerView.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.HelperClasses.CircleTransform;
import uur.com.pinbook.RecyclerView.Model.FeedAllItem;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;

public class FeedAllItemAdapter extends RecyclerView.Adapter<FeedAllItemAdapter.CustomViewHolder> {

    private Context context;
    private List<FeedAllItem> feedList;
    private RecyclerViewClickListener mListener;
    private InnerRecyclerViewClickListener mListener2;

    public FeedAllItemAdapter(Context context, RecyclerViewClickListener listener, InnerRecyclerViewClickListener listener2) {
        this.context = context;
        this.feedList = new ArrayList<>();
        this.mListener = listener;
        this.mListener2 = listener2;
    }

    public void clear() {

        final int size = feedList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                feedList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_single_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view, mListener);

//        view.setOnClickListener(this);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(FeedAllItemAdapter.CustomViewHolder holder, int position) {


        Log.i("--> position ", (Integer.toString(position)));
        final FeedAllItem feed = feedList.get(position);

        /*
        Picasso.with(context)
                .load(datum.getImage())
                .into(holder.fullImage);
        */

        //feed name text
        holder.nameTextView.setText("Title");

        //feed porfile picture
        Picasso.with(context)
                .load(feed.getOwnerPictureUrl())
                .transform(new CircleTransform())
                .into(holder.profileImage);

        //feed profile Name
        holder.profileName.setText(feed.getOwnerName());

        //feed Items
        ArrayList feedPinItems = feed.getFeedPinItems();

        FeedPinItemAdapter.RecyclerViewClickListener2 recyclerViewClickListener2 = new FeedPinItemAdapter.RecyclerViewClickListener2() {
            @Override
            public void onClick(View view, FeedPinItem singleItem) {
                //Toast.makeText(context, "Position " + position, Toast.LENGTH_SHORT).show();

                mListener2.onClick(view, singleItem, feed);
            }
        };

        FeedPinItemAdapter itemListDataAdapter = new FeedPinItemAdapter(context, feedPinItems, recyclerViewClickListener2);

        holder.horizontalRecyclerView.setHasFixedSize(true);
        holder.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalRecyclerView.setAdapter(itemListDataAdapter);
        holder.horizontalRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return (feedList != null ? feedList.size() : 0);
    }

    public void addAll(FeedAllItem fa) {
        int initialSize = feedList.size();

        //List<FeedAllItem> fa2 = new ArrayList<>();
        //fa2.add(fa.get(fa.size()-1));

        feedList.add(fa);
        notifyItemRangeInserted(initialSize, initialSize + 1);
    }

    public String getLastItemId() {
        if (feedList.size() > 0)
            return feedList.get(feedList.size() - 1).getLocationId();
        else
            return "0";
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView profileImage;
        TextView nameTextView;
        TextView profileName;
        TextView timeStamp;
        CardView cardView;
        RecyclerView horizontalRecyclerView;
        private RecyclerViewClickListener mListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView, RecyclerViewClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            profileName = (TextView) itemView.findViewById(R.id.profile_name);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontalRecycleView);

            mListener = listener;


            cardView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            //Toast.makeText(context, feedList.get(clickedPosition).getOwnerName(), Toast.LENGTH_LONG).show();
            //feedList.get(clickedPosition)
            Toast.makeText(context, "Ana method", Toast.LENGTH_LONG).show();

            //Log.i("info", "All card clicked..");
            mListener.onClick(v, feedList.get(clickedPosition));


        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, FeedAllItem feedAllItem);
    }


    public interface InnerRecyclerViewClickListener {

        void onClick(View view, FeedPinItem singleItem, FeedAllItem feedAllItem);
    }



}
