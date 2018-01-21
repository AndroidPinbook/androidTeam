package uur.com.pinbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.CardView.CardAdapter;
import uur.com.pinbook.CardView.CardFragmentPagerAdapter;
import uur.com.pinbook.CardView.ShadowTransformer;
import uur.com.pinbook.R;
import butterknife.ButterKnife;

import uur.com.pinbook.fragments.InnerFragments.SingleLocationFragment;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.btn_click_me)
    Button btnClickMe;

    int fragCount;
    View view;
    private RecyclerView recycler_view;
    private List<CardFragmentPagerAdapter> location_list;

    public static ProfileFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ProfileFragment() {
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

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            fragCount = args.getInt(ARGS_INSTANCE);
        }


        //initCardView();


        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);

        for(int i= 0; i<3 ; i++){
            
        }


        return view;
    }

    private void initCardView() {

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getChildFragmentManager(), dpToPixels(2, getContext()));
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

    /**
     * Change value in dp to pixels
     *
     * @param dp
     * @param context
     * @return
     */
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(SingleLocationFragment.newInstance(fragCount + 1));

                }
            }
        });


        ((ProfilePageActivity) getActivity()).updateToolbarTitle((fragCount == 0) ? "Profile" : "Sub Profile " + fragCount);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
