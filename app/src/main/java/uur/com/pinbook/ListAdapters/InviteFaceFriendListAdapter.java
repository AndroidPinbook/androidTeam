package uur.com.pinbook.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.JavaFiles.Friend;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.FirebaseConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class InviteFaceFriendListAdapter extends RecyclerView.Adapter<InviteFaceFriendListAdapter.MyViewHolder>{

    private ArrayList<Friend> data;
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;

    LayoutInflater layoutInflater;

    Context context;
    Activity activity;

    public InviteFaceFriendListAdapter(Context context, ArrayList<Friend> friendList) {
        layoutInflater = LayoutInflater.from(context);
        data=friendList;
        Collections.sort(data, new CustomComparator());
        this.context = context;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
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
    public InviteFaceFriendListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.invite_outbnd_ver_list_item, parent, false);
        final InviteFaceFriendListAdapter.MyViewHolder holder = new InviteFaceFriendListAdapter.MyViewHolder(view);
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

                    //InviteOutbound update children
                    DatabaseReference mdbRef = FirebaseDatabase.getInstance().getReference(InviteFacebookOutbound).child(
                            FirebaseGetAccountHolder.getUserID());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(selectedFriend.getUserID(), " ");
                    mdbRef.updateChildren(map);

                    //InviteInbound update children
                    DatabaseReference mdbRef2 = FirebaseDatabase.getInstance().getReference(InviteFacebookInbound).child(
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
                    DatabaseReference mdbRef2 = FirebaseDatabase.getInstance().getReference(InviteFacebookInbound).child(
                            FirebaseGetAccountHolder.getUserID()).child(selectedFriend.getUserID());
                    mdbRef2.removeValue();

                    //Remove from inviteOutbound
                    DatabaseReference mdbRef3 = FirebaseDatabase.getInstance().getReference(InviteFacebookOutbound).
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
    public void onBindViewHolder(InviteFaceFriendListAdapter.MyViewHolder holder, int position) {
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