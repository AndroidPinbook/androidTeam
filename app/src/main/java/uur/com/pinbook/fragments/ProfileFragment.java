package uur.com.pinbook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.EditProfileActivity;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteContactOutbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookInbounds;
import uur.com.pinbook.FirebaseGetData.FBGetInviteFacebookOutbounds;
import uur.com.pinbook.FirebaseGetData.FirebaseGetAccountHolder;
import uur.com.pinbook.OtherFragments.ProfileDetailFragment;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.StringConstant.ProfileTitle;
import static uur.com.pinbook.ConstantsModel.StringConstant.profileDetailFragment;

public class ProfileFragment extends BaseFragment{

    private Button btnClickMe;
    private Button editProfileButton;
    private ImageView openSettingsImgv;

    private View mView;

    private FrameLayout profileMainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mView);

        btnClickMe = mView.findViewById(R.id.btn_click_me);
        editProfileButton = mView.findViewById(R.id.editProfileButton);
        openSettingsImgv = mView.findViewById(R.id.openSettingsImgv);

       // btnClickMe.setOnClickListener(this);
       // editProfileButton.setOnClickListener(this);
       // openSettingsImgv.setOnClickListener(this);

        profileMainLayout = mView.findViewById(R.id.profileMainLayout);

        FBGetInviteFacebookInbounds.getInstance();
        FBGetInviteFacebookOutbounds.getInstance();
        FBGetInviteContactInbounds.getInstance();
        FBGetInviteContactOutbounds.getInstance();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        openSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDetailFragment nextFrag = new ProfileDetailFragment(getActivity());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, profileDetailFragment)
                        .addToBackStack(profileDetailFragment)
                        .commit();
            }
        });

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ProfilePageActivity)getActivity()).updateToolbarTitle(ProfileTitle);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
