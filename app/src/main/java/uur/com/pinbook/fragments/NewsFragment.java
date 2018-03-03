package uur.com.pinbook.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.OtherFragments.ProfileDetailFragment;
import uur.com.pinbook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;
import static uur.com.pinbook.ConstantsModel.StringConstant.PinThrowTitle;
import static uur.com.pinbook.ConstantsModel.StringConstant.ProfileTitle;
import static uur.com.pinbook.ConstantsModel.StringConstant.profileDetailFragment;


public class NewsFragment extends BaseFragment implements View.OnClickListener{

    private Button btnClickMe;

    private View mView;

    private FrameLayout newsMainLayout;

    /*public static NewsFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, mView);

        btnClickMe = (Button) mView.findViewById(R.id.btn_click_me);
        btnClickMe.setOnClickListener(this);

        newsMainLayout = (FrameLayout)mView.findViewById(R.id.newsMainLayout);

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

    @Override
    public void onClick(View v) {

        int i = v.getId();

        switch (i) {
            case R.id.btn_click_me:

                ProfileDetailFragment nextFrag = new ProfileDetailFragment(getActivity());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, profileDetailFragment)
                        .addToBackStack(profileDetailFragment)
                        .commit();


                break;
            default:
                break;
        }
    }


}
