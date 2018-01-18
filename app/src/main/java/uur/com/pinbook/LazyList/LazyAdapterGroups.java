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
import uur.com.pinbook.DefaultModels.SelectedGroupList;
import uur.com.pinbook.JavaFiles.Group;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.*;

public class LazyAdapterGroups extends RecyclerView.Adapter<LazyAdapterGroups.MyViewHolder>{

    private ArrayList<Group> data;
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    SelectedGroupList selectedGroupList;

    Context context;

    public LazyAdapterGroups(Context context, ArrayList<Group> groupList) {
        layoutInflater = LayoutInflater.from(context);
        data=groupList;
        this.context = context;
        imageLoader=new ImageLoader(context.getApplicationContext(), groupsCacheDirectory);
        selectedGroupList = SelectedGroupList.getInstance();
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public LazyAdapterGroups.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_list_item, parent, false);

        LazyAdapterGroups.MyViewHolder holder = new LazyAdapterGroups.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView specialName;
        CheckBox selectCheckBox;
        Group selectedGroup;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            specialName = (TextView) view.findViewById(R.id.specialNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    }
                    else {
                        selectCheckBox.setChecked(true);
                        selectedGroupList.addGroup(selectedGroup);
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectCheckBox.isChecked()) {
                        selectedGroupList.addGroup(selectedGroup);
                    }
                    else {
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    }
                }
            });
        }

        public void setData(Group selectedGroup, int position) {

            this.specialName.setText(selectedGroup.getGroupName());
            this.position = position;
            this.selectedGroup = selectedGroup;
            imageLoader.DisplayImage(selectedGroup.getPictureUrl(), specialProfileImgView);
            selectCheckBox.setChecked(false);
        }
    }

    @Override
    public void onBindViewHolder(LazyAdapterGroups.MyViewHolder holder, int position) {
        Group selectedGroup = data.get(position);
        holder.setData(selectedGroup, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  data.size();
    }
}