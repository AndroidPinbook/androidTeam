package uur.com.pinbook.LazyList;

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
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class LazyAdapterFriends extends RecyclerView.Adapter<LazyAdapterFriends.MyViewHolder>{

    private ArrayList<Friend> data;
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    SelectedFriendList selectedFriendList;

    Context context;

    public LazyAdapterFriends(Context context, ArrayList<Friend> friendList) {
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
    public LazyAdapterFriends.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_list_item, parent, false);

        LazyAdapterFriends.MyViewHolder holder = new LazyAdapterFriends.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        CheckBox selectCheckBox;
        Friend selectedFriend;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        selectedFriendList.removeFriend(selectedFriend.getUserID());
                    }
                    else {
                        selectCheckBox.setChecked(true);
                        selectedFriendList.addFriend(selectedFriend);
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectCheckBox.isChecked()) {
                        selectedFriendList.addFriend(selectedFriend);
                    }
                    else {
                        selectedFriendList.removeFriend(selectedFriend.getUserID());
                    }
                }
            });
        }

        public void setData(Friend selectedFriend, int position) {

            this.userNameSurname.setText(selectedFriend.getNameSurname());
            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePicSrc(), specialProfileImgView);
            selectCheckBox.setChecked(false);
        }
    }

    @Override
    public void onBindViewHolder(LazyAdapterFriends.MyViewHolder holder, int position) {
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