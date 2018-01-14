package uur.com.pinbook.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import uur.com.pinbook.Activities.EnterPageActivity;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.DefaultModels.Person;
import uur.com.pinbook.R;

/**
 * Created by mac on 13.01.2018.
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {

    ArrayList<Person> personList;
    LayoutInflater layoutInflater;
    View view;
    View horView;
    Context context;
    private ImageView userProfileImgView;
    LinearLayout personListLinearLayout;
    CardView personVertCardView;

    public static ArrayList<Person> selectedPersonList;

    public PersonListAdapter(Context context, ArrayList<Person> personList) {



        layoutInflater = LayoutInflater.from(context);

        this.personList = personList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.person_list_item, parent, false);
        horView = layoutInflater.inflate(R.layout.person_list_horizontal_item, parent, false);

        selectedPersonList = new ArrayList<Person>();

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Person selectedPerson = personList.get(position);
        holder.setData(selectedPerson, position);

    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void addSelectedPerson(Person addedPerson){
        selectedPersonList.add(addedPerson);
        //notifyItemInserted(position);
        //notifyItemRangeChanged(position, selectedPersonList.size());
    }

    public void deleteSelectedPerson(String userID){

        int index = 0;

        for(index = 0; index < selectedPersonList.size(); index++){
            Person person = selectedPersonList.get(index);
            if(person.getUserID() == userID) {
                selectedPersonList.remove(index);
                break;
            }
        }

        //notifyItemRemoved(index);
        //notifyItemRangeChanged(index, selectedPersonList.size());
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        CheckBox selectCheckBox;
        Person selectedPerson;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            userProfileImgView = (ImageView) view.findViewById(R.id.personProfPicImgView);
            userNameSurname = (TextView) view.findViewById(R.id.personNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            personListLinearLayout = (LinearLayout) view.findViewById(R.id.personListLinearLayout);
            personVertCardView = (CardView) view.findViewById(R.id.personRootCardView);

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(selectCheckBox.isChecked()){
                        addSelectedPerson(selectedPerson);
                    }else{
                        deleteSelectedPerson(selectedPerson.getUserID());
                    }
                }
            });

            personListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        deleteSelectedPerson(selectedPerson.getUserID());
                    }
                    else {
                        selectCheckBox.setChecked(true);
                        addSelectedPerson(selectedPerson);
                    }
                }
            });


            personVertCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void setData(Person selectedPerson, int position) {

            this.userNameSurname.setText(selectedPerson.getUserNameSurname());
            this.position = position;
            this.selectedPerson = selectedPerson;


            if (!selectedPerson.getImageUriText().equals(" ")) {
                try {
                    DownloadTask taskManager = new DownloadTask();
                    taskManager.execute(selectedPerson.getImageUriText()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream profileImageStream = urlConnection.getInputStream();
                Bitmap photo = BitmapFactory.decodeStream(profileImageStream);
                photo = BitmapConversion.getRoundedShape(photo, 600, 600, null);

                userProfileImgView.setImageBitmap(photo);
                return result;

            } catch (Exception e) {

                Log.i("Info", "  >>DownloadTask error:" + e.toString());
                return result;
            }
        }
    }

}
