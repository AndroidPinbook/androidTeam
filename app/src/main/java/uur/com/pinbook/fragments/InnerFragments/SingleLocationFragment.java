package uur.com.pinbook.fragments.InnerFragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import uur.com.pinbook.Activities.ProfilePageActivity;
import uur.com.pinbook.CardView.CardFragmentPagerAdapter;
import uur.com.pinbook.CardView.ShadowTransformer;
import uur.com.pinbook.R;
import uur.com.pinbook.fragments.BaseFragment;


public class SingleLocationFragment extends BaseFragment {
    View mView;

    int fragCount;


    public static SingleLocationFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        SingleLocationFragment fragment = new SingleLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public SingleLocationFragment() {
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

        mView = inflater.inflate(R.layout.fragment_single_location, container, false);

        ButterKnife.bind(this, mView);

        ((ProfilePageActivity) getActivity()).updateToolbarTitle("Single Activity");

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ViewPager viewPager = (ViewPager) mView.findViewById(R.id.viewPager);

        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(), dpToPixels(2, getActivity()));
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

    /**
     * Change value in dp to pixels
     * @param dp
     * @param context
     * @return
     */
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}
