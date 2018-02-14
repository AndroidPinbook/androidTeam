package uur.com.pinbook.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class FriendVerticalListAdapter extends RecyclerView.Adapter<FriendVerticalListAdapter.MyViewHolder> implements Filterable{

    private ArrayList<Friend> data;
    private ArrayList<Friend> dataCopy = new ArrayList<Friend>();
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    SelectedFriendList selectedFriendList;
    String searchText;

    Context context;
    Activity activity;

    public FriendVerticalListAdapter(Context context, ArrayList<Friend> friendList, String searchText) {
        layoutInflater = LayoutInflater.from(context);
        data=friendList;
        dataCopy.addAll(data);
        Collections.sort(data, new CustomComparator());
        this.context = context;
        this.searchText = searchText;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        selectedFriendList = SelectedFriendList.getInstance();

        if(searchText != null) {
            if (!searchText.isEmpty())
                getFilter().filter(searchText);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    dataCopy = data;
                } else {
                    ArrayList<Friend> tempFilteredList = new ArrayList<>();

                    for (Friend friend : data) {

                        if (friend.getNameSurname().toLowerCase().contains(searchString.toLowerCase())) {
                            tempFilteredList.add(friend);
                        }
                    }
                    dataCopy = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataCopy;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataCopy = (ArrayList<Friend>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CustomComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getNameSurname().compareToIgnoreCase(o2.getNameSurname());
        }
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public FriendVerticalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_vertical_list_item, parent, false);
        final FriendVerticalListAdapter.MyViewHolder holder = new FriendVerticalListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        CheckBox selectCheckBox;
        Friend selectedFriend;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyBoard(itemView);
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
                    hideKeyBoard(itemView);
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
            imageLoader.DisplayImage(selectedFriend.getProfilePicSrc(), specialProfileImgView, displayRounded);
            selectCheckBox.setChecked(false);
        }
    }

    @Override
    public void onBindViewHolder(FriendVerticalListAdapter.MyViewHolder holder, int position) {
        Friend selectedFriend = dataCopy.get(position);
        holder.setData(selectedFriend, position);
    }

    public void hideKeyBoard(View view){
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  dataCopy.size();
    }
}