package uur.com.pinbook.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.DefaultModels.SelectedFriendList;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class InviteOutboundVerListAdapter extends RecyclerView.Adapter<InviteOutboundVerListAdapter.MyViewHolder> implements Filterable{

    private ArrayList<Friend> data;
    private ArrayList<Friend> dataCopy = new ArrayList<Friend>();
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;

    LayoutInflater layoutInflater;
    String searchText;

    Context context;
    Activity activity;

    public InviteOutboundVerListAdapter(Context context, ArrayList<Friend> friendList, String searchText) {
        layoutInflater = LayoutInflater.from(context);
        data=friendList;
        dataCopy.addAll(data);
        Collections.sort(data, new CustomComparator());
        this.context = context;
        this.searchText = searchText;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);

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
    public InviteOutboundVerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.invite_outbnd_ver_list_item, parent, false);
        final InviteOutboundVerListAdapter.MyViewHolder holder = new InviteOutboundVerListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        Friend selectedFriend;
        Button sendInviteButton;
        Button invSendedButton;
        Button approveInvitationBtn;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            sendInviteButton = (Button) view.findViewById(R.id.sendInviteButton);
            invSendedButton = (Button) view.findViewById(R.id.invSendedButton);
            approveInvitationBtn = (Button) view.findViewById(R.id.approveInvitationBtn);

            sendInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendInviteButton.setVisibility(View.GONE);
                    approveInvitationBtn.setVisibility(View.GONE);
                    invSendedButton.setVisibility(View.VISIBLE);

                    DatabaseReference mdbRef = FirebaseDatabase.getInstance().getReference(InviteOutbound).child(
                            FirebaseGetAccountHolder.getUserID());

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(selectedFriend.getUserID(), " ");
                    mdbRef.updateChildren(map);

                    DatabaseReference mdbRef2 = FirebaseDatabase.getInstance().getReference(InviteInbound).child(
                            selectedFriend.getUserID());

                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put(FirebaseGetAccountHolder.getUserID(), " ");
                    mdbRef2.updateChildren(map2);
                }
            });

            approveInvitationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Friend added to Friends child
                    DatabaseReference mdbRef = FirebaseDatabase.getInstance().getReference(Friends).child(
                            FirebaseGetAccountHolder.getUserID()).child(selectedFriend.getUserID());
                    Map<String, Object> map = new HashMap<String, Object>();

                    map.put(nameSurname, selectedFriend.getNameSurname());
                    map.put(profilePictureUrl, selectedFriend.getProfilePicSrc());
                    map.put(providerId, selectedFriend.getProviderId());
                    mdbRef.updateChildren(map);

                    //Remove from inviteInbound
                    DatabaseReference mdbRef2 = FirebaseDatabase.getInstance().getReference(InviteInbound).child(
                            FirebaseGetAccountHolder.getUserID()).child(selectedFriend.getUserID());
                    mdbRef2.removeValue();

                    //Remove from inviteOutbound
                    DatabaseReference mdbRef3 = FirebaseDatabase.getInstance().getReference(InviteOutbound).
                            child(selectedFriend.getUserID()).child(FirebaseGetAccountHolder.getUserID());
                    mdbRef3.removeValue();

                    //Add user to Friends child
                    DatabaseReference mdbRef4 = FirebaseDatabase.getInstance().getReference(Friends).child(
                            selectedFriend.getUserID()).child(FirebaseGetAccountHolder.getUserID());
                    Map<String, Object> map2 = new HashMap<String, Object>();

                    String fullName = FirebaseGetAccountHolder.getInstance().getUser().getName() + " " +
                            FirebaseGetAccountHolder.getInstance().getUser().getSurname();

                    map2.put(nameSurname, fullName);
                    map2.put(profilePictureUrl, FirebaseGetAccountHolder.getInstance().getUser().getProfilePicSrc());
                    map2.put(providerId, FirebaseGetAccountHolder.getInstance().getUser().getProviderId());
                    mdbRef4.updateChildren(map2);

                    sendInviteButton.setVisibility(View.GONE);
                    invSendedButton.setVisibility(View.GONE);
                    approveInvitationBtn.setVisibility(View.GONE);
                }
            });
        }

        public void setData(Friend selectedFriend, int position) {

            if(selectedFriend.getFriendStatus().equals(OutbndWaiting)){
                sendInviteButton.setVisibility(View.GONE);
                invSendedButton.setVisibility(View.VISIBLE);
                approveInvitationBtn.setVisibility(View.GONE);
            }else if(selectedFriend.getFriendStatus().equals(InbndWaiting)){
                sendInviteButton.setVisibility(View.GONE);
                invSendedButton.setVisibility(View.GONE);
                approveInvitationBtn.setVisibility(View.VISIBLE);
            } else if(selectedFriend.getFriendStatus().equals(No)){
                sendInviteButton.setVisibility(View.VISIBLE);
                invSendedButton.setVisibility(View.GONE);
                approveInvitationBtn.setVisibility(View.GONE);
            }else if(selectedFriend.getFriendStatus().equals(Yes)){
                sendInviteButton.setVisibility(View.GONE);
                invSendedButton.setVisibility(View.GONE);
                approveInvitationBtn.setVisibility(View.GONE);
            }

            this.userNameSurname.setText(selectedFriend.getNameSurname());
            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePicSrc(), specialProfileImgView, displayRounded);
        }
    }

    @Override
    public void onBindViewHolder(InviteOutboundVerListAdapter.MyViewHolder holder, int position) {
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