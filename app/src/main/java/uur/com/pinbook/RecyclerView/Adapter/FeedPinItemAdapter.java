package uur.com.pinbook.RecyclerView.Adapter;

/**
 * Created by pratap.kesaboyina on 24-12-2014.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import uur.com.pinbook.R;
import uur.com.pinbook.RecyclerView.Model.FeedPinItem;


public class FeedPinItemAdapter extends RecyclerView.Adapter<FeedPinItemAdapter.SingleItemRowHolder> {

    private ArrayList<FeedPinItem> itemsList;
    private Context mContext;
    private RecyclerViewClickListener2 mListener;

    public FeedPinItemAdapter(Context context, ArrayList<FeedPinItem> itemsList, RecyclerViewClickListener2 listener) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.mListener = listener;

    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_inner_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v, mListener);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        FeedPinItem singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());

        //feed porfile picture
        Picasso.with(mContext)
                .load(singleItem.getUrl())
                .into(holder.itemImage);

       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;

        protected ImageView itemImage;
        private RecyclerViewClickListener2 mListener;

        public SingleItemRowHolder(View view, RecyclerViewClickListener2 listener) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
            mListener = listener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int clickedPosition = getAdapterPosition();
                    FeedPinItem singleItem = itemsList.get(clickedPosition);
                    //Log.i("Clicked_image_url :", singleItem.getUrl());
                    //Toast.makeText(v.getContext(), singleItem.getUrl(), Toast.LENGTH_SHORT).show();

                    mListener.onClick(v, singleItem);

                }
            });


        }

    }

    public interface RecyclerViewClickListener2 {

        void onClick(View view, FeedPinItem singleItem);
    }

}