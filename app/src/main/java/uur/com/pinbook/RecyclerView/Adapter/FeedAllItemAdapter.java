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

public class FeedAllItemAdapter extends RecyclerView.Adapter<FeedAllItemAdapter.CustomViewHolder> {

    private Context context;
    private List<FeedAllItem> feedList;

    public FeedAllItemAdapter(Context context) {
        this.context = context;
        this.feedList = new ArrayList<>();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_single_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

//        view.setOnClickListener(this);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(FeedAllItemAdapter.CustomViewHolder holder, int position) {


        Log.i("--> position ", (Integer.toString(position)));
        FeedAllItem feed = feedList.get(position);

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
        FeedPinItemAdapter itemListDataAdapter = new FeedPinItemAdapter(context, feedPinItems);

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
        notifyItemRangeInserted(initialSize, initialSize+1);
    }

    public String getLastItemId() {
        return feedList.get(feedList.size() - 1).getLocationId();
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

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        CustomViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            profileName = (TextView) itemView.findViewById(R.id.profile_name);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontalRecycleView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Toast.makeText(context, feedList.get(clickedPosition).getOwnerName(), Toast.LENGTH_LONG).show();
        }
    }
}
