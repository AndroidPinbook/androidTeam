package uur.com.pinbook.ListAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uur.com.pinbook.Activities.ProfilePhotoActivity;
import uur.com.pinbook.Adapters.CustomDialogAdapter;
import uur.com.pinbook.ConstantsModel.StringConstant;
import uur.com.pinbook.DefaultModels.SelectedGroupList;
import uur.com.pinbook.FirebaseAdapters.FirebaseDeleteGroupAdapter;
import uur.com.pinbook.FirebaseGetData.FirebaseGetGroups;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.LazyList.ImageLoader;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class GroupVerticalListAdapter extends RecyclerView.Adapter<GroupVerticalListAdapter.MyViewHolder> {

    private ArrayList<Group> data = new ArrayList<>();
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    SelectedGroupList selectedGroupList;
    Context context;

    public static final int groupDetailItem = 0;
    public static final int groupDeleteItem = 1;

    public GroupVerticalListAdapter(Context context, HashMap<String, Group> groupListMap) {
        layoutInflater = LayoutInflater.from(context);
        fillGroupArray(groupListMap);
        this.context = context;
        imageLoader = new ImageLoader(context.getApplicationContext(), groupsCacheDirectory);
        selectedGroupList = SelectedGroupList.getInstance();
    }

    private void fillGroupArray(HashMap<String, Group> groupListMap) {
        Iterator it = groupListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Group group = (Group) pair.getValue();
            data.add(group);
            it.remove();
        }
    }

    @Override
    public GroupVerticalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_vertical_list_item, parent, false);
        GroupVerticalListAdapter.MyViewHolder holder = new GroupVerticalListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView specialNameTextView;
        TextView itemIdTextView;
        TextView adminIDTextView;
        CheckBox selectCheckBox;
        Group selectedGroup;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            specialNameTextView = (TextView) view.findViewById(R.id.specialNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);
            itemIdTextView = (TextView) view.findViewById(R.id.itemIdTextView);
            adminIDTextView = (TextView) view.findViewById(R.id.adminIDTextView);

            //String gID = selectedGroup.getGroupID();
            //selectedGroup.setUserIDList(getUserIDList(gID));

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedGroup = data.get(position);
                    if (selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    } else {
                        selectCheckBox.setChecked(true);
                        selectedGroupList.addGroup(selectedGroup);
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedGroup = data.get(position);
                    if (selectCheckBox.isChecked()) {
                        selectedGroupList.addGroup(selectedGroup);
                    } else {
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    }
                }
            });

            specialListLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i("Info", "Long click");
                    Log.i("Info", "view:" + v.toString());

                    selectedGroup = data.get(position);
                    ViewGroup viewGroup = (ViewGroup) v;

                    boolean groupIDFoundInd = false;
                    boolean groupNameFoundInd = false;
                    boolean groupAdminFoundInd = false;

                    String foundedGroupId = null;
                    String foundedGroupName = null;
                    String foundedAdminID = null;

                    Log.i("Info", "viewgroup:" + viewGroup.toString());

                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View view = viewGroup.getChildAt(i);

                        if (itemIdTextView == view.findViewById(R.id.itemIdTextView)) {
                            TextView groupIdTv = view.findViewById(R.id.itemIdTextView);
                            foundedGroupId = groupIdTv.getText().toString();
                            groupIDFoundInd = true;
                        }

                        if (adminIDTextView == view.findViewById(R.id.adminIDTextView)) {
                            TextView groupAdminID = view.findViewById(R.id.adminIDTextView);
                            foundedAdminID = groupAdminID.getText().toString();
                            groupNameFoundInd = true;
                        }

                        if (specialNameTextView == view.findViewById(R.id.specialNameTextView)) {
                            TextView groupNameTv = view.findViewById(R.id.specialNameTextView);
                            foundedGroupName = groupNameTv.getText().toString();
                            groupAdminFoundInd = true;
                        }
                    }

                    if (!groupIDFoundInd || !groupNameFoundInd || !groupAdminFoundInd) {
                        CustomDialogAdapter.showErrorDialog(context, "Grup secilemedi!");
                        return false;
                    }

                    showGroupDetail(foundedGroupId, foundedGroupName, foundedAdminID,
                            position, selectedGroup);

                    return true;
                }
            });
        }

        //public ArrayList<String> getUserIDList(String groupSelectedID){


        //}

        public void setData(Group selectedGroup, int position) {

            this.specialNameTextView.setText(selectedGroup.getGroupName());
            this.itemIdTextView.setText(selectedGroup.getGroupID());
            this.adminIDTextView.setText(selectedGroup.getAdminID());
            this.position = position;
            this.selectedGroup = selectedGroup;
            imageLoader.DisplayImage(selectedGroup.getPictureUrl(), specialProfileImgView);
            selectCheckBox.setChecked(false);
        }
    }

    public String getFbUserID() {

        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser.getUid();
    }

    private void showGroupDetail(final String groupID, String groupName,
                                 final String adminID, final int position,
                                 final Group selectedGroup) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  Grup Bilgileri");

        Log.i("Info", "adminID :" + adminID);
        Log.i("Info", "FbUserID:" + getFbUserID());

        if (adminID.equals(getFbUserID()))
            adapter.add("  Grubu Sil");
        else
            adapter.add("  Gruptan Cik");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(groupName);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == groupDetailItem) {


                } else if (item == groupDeleteItem) {

                    if (adminID.equals(getFbUserID())) {
                        removeItemFromGroupList(position);
                        FirebaseGetGroups.getInstance(adminID).removeGroupFromList(selectedGroup.getGroupID());
                        deleteGroup(selectedGroup);
                    }
                    //else
                    //exitFromGroup();


                } else {
                    Toast.makeText(context, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void removeItemFromGroupList(int position) {

        data.remove(position);
        notifyDataSetChanged();
    }

    public void deleteGroup(Group selectedGroup) {
        FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter = new FirebaseDeleteGroupAdapter(selectedGroup, deleteGroup);
    }

    @Override
    public void onBindViewHolder(GroupVerticalListAdapter.MyViewHolder holder, int position) {
        Group selectedGroup = data.get(position);
        holder.setData(selectedGroup, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}