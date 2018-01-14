package uur.com.pinbook.SpecialFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import uur.com.pinbook.Adapters.PersonListAdapter;
import uur.com.pinbook.DefaultModels.Person;
import uur.com.pinbook.R;

import static uur.com.pinbook.JavaFiles.ConstValues.Friends;
import static uur.com.pinbook.JavaFiles.ConstValues.Users;
import static uur.com.pinbook.JavaFiles.ConstValues.name;
import static uur.com.pinbook.JavaFiles.ConstValues.profilePictureUrl;
import static uur.com.pinbook.JavaFiles.ConstValues.surname;

/**
 * Created by mac on 13.01.2018.
 */

@SuppressLint("ValidFragment")
public class PersonFragment extends Fragment{

    RecyclerView personRecyclerView;
    RecyclerView personHorRecyclerView;

    private View mView;

    String FBuserID;

    LinearLayoutManager linearLayoutManager;
    ArrayList<Person> personList;
    PersonListAdapter personListAdapter;

    @SuppressLint("ValidFragment")
    public PersonFragment(String FBuserID) {
        this.FBuserID = FBuserID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_person_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.personRecyclerView);
        personHorRecyclerView = (RecyclerView) mView.findViewById(R.id.personHorizontalRecyclerView);
        getData(FBuserID);

        personRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "personRecyclerView clicked");

            }
        });
    }

    public void getData(String userID){

        personList = new ArrayList<Person>();

        DatabaseReference mDbrefFriendList = FirebaseDatabase.getInstance().getReference(Friends).child(userID);

        ValueEventListener valueEventListenerForFriendList = mDbrefFriendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(personList != null)
                    personList.clear();

                for(DataSnapshot friendsSnapshot: dataSnapshot.getChildren()){

                    final Person person = new Person();
                    String friendUserID = friendsSnapshot.getKey();
                    person.setUserID(friendUserID);

                    Log.i("Info", "  >>friendUserID:" + friendUserID);

                    final DatabaseReference mDbrefDetails = FirebaseDatabase.getInstance().getReference(Users).child(friendUserID);

                    ValueEventListener valueEventListenerForDetails = mDbrefDetails.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){

                                Map<String , String> map = new HashMap<String, String>();

                                map = (Map) dataSnapshot.getValue();

                                String nameSurname = map.get(name) + " " + map.get(surname);
                                person.setUserNameSurname(nameSurname);

                                person.setImageUriText(map.get(profilePictureUrl));

                                Log.i("Info", "  >>nameSurname:" + nameSurname);
                                Log.i("Info", "  >>profilePictureUrl:" + map.get(profilePictureUrl));

                                personList.add(person);

                                personListAdapter = new PersonListAdapter(getActivity(), personList);
                                personRecyclerView.setAdapter(personListAdapter);

                                linearLayoutManager  = new LinearLayoutManager(getActivity());
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                personRecyclerView.setLayoutManager(linearLayoutManager);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
