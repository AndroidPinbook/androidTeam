package uur.com.pinbook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;

import uur.com.pinbook.Activities.EnterPageActivity;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends BaseFragment {


    @BindView(R.id.btn_click_me)
    Button btnClickMe;

    int fragCount;
    View view;

    private FirebaseAuth firebaseAuth;

    @BindView(R.id.buttonLogout)
    Button buttonLogout;

    public static HomeFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);


        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            fragCount = args.getInt(ARGS_INSTANCE);
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), EnterPageActivity.class);
            startActivity(intent);
        }


        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();

                LoginManager.getInstance().logOut();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                Intent intent = new Intent(getActivity(), EnterPageActivity.class);
                startActivity(intent);
            }
        });



        btnClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(HomeFragment.newInstance(fragCount + 1));

                }
            }
        });


        ( (ProfilePageActivity)getActivity()).updateToolbarTitle((fragCount == 0) ? "Home" : "Sub Home "+fragCount);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
