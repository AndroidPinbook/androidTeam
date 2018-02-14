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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.HelperClasses.CircleTransform;
import uur.com.pinbook.RecyclerView.Model.SingleFeed;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CustomViewHolder> {

    private Context context;
    private List<SingleFeed> feedList;

    public FeedAdapter(Context context, List<SingleFeed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_single_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

//        view.setOnClickListener(this);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(FeedAdapter.CustomViewHolder holder, int position) {


        Log.i("--> position ", (Integer.toString(position)));
        SingleFeed feed = feedList.get(position);

        /*
        Picasso.with(context)
                .load(datum.getImage())
                .into(holder.fullImage);
        */

        //feed name text
        holder.nameTextView.setText(feed.getTitle());

        //feed porfile picture
        Picasso.with(context)
                .load(feed.getNameImage())
                .transform(new CircleTransform())
                .into(holder.profileImage);

        //feed profile Name
        holder.profileName.setText(feed.getName());

        //feed Items
        ArrayList singleFeedItems = feedList.get(position).getAllItemsInSingleFeed();
        FeedInnerItemDataAdapter itemListDataAdapter = new FeedInnerItemDataAdapter(context, singleFeedItems);

        holder.horizontalRecyclerView.setHasFixedSize(true);
        holder.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalRecyclerView.setAdapter(itemListDataAdapter);
        holder.horizontalRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return (feedList != null ? feedList.size() : 0);
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
            Toast.makeText(context, feedList.get(clickedPosition).getName(), Toast.LENGTH_LONG).show();
        }
    }
}
