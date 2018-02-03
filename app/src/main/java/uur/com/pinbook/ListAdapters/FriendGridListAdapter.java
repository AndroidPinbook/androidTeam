package uur.com.pinbook.ListAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class FriendGridListAdapter extends RecyclerView.Adapter<FriendGridListAdapter.MyViewHolder>{

    private ArrayList<Friend> data;
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;

    LayoutInflater layoutInflater;
    SelectedFriendList selectedFriendList;

    Context context;

    public FriendGridListAdapter(Context context, ArrayList<Friend> friendList) {
        layoutInflater = LayoutInflater.from(context);
        data=friendList;
        this.context = context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        selectedFriendList = SelectedFriendList.getInstance();
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public FriendGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_grid_list_item, parent, false);

        FriendGridListAdapter.MyViewHolder holder = new FriendGridListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        Friend selectedFriend;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
        }

        public void setData(Friend selectedFriend, int position) {

            String username = selectedFriend.getNameSurname();

            if(username.trim().length() > 16){
                username = username.trim().substring(0, 13) + "...";
            }

            this.userNameSurname.setText(username);
            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePicSrc(), specialProfileImgView);
        }
    }

    @Override
    public void onBindViewHolder(FriendGridListAdapter.MyViewHolder holder, int position) {
        Friend selectedFriend = data.get(position);
        holder.setData(selectedFriend, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  data.size();
    }
}