package uur.com.pinbook.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uur.com.pinbook.R;
import butterknife.ButterKnife;

import uur.com.pinbook.RecyclerView.Adapter.HorizontalAdapter;
import uur.com.pinbook.RecyclerView.Adapter.VerticalAdapter;
import uur.com.pinbook.RecyclerView.HelperClasses.GridSpacingItemDecoration;
import uur.com.pinbook.RecyclerView.HelperClasses.NetworkCheckingClass;
import uur.com.pinbook.RecyclerView.Interface.ApiInterface;
import uur.com.pinbook.RecyclerView.Model.Datum;
import uur.com.pinbook.RecyclerView.Model.JsonData;
import uur.com.pinbook.RecyclerView.Model.Popular;
import uur.com.pinbook.RecyclerView.Retrofit.RetrofitApiClient;


import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends BaseFragment {


    int fragCount;
    View view;

    RecyclerView recyclerViewHorizontal;
    RecyclerView recyclerViewVertical;
    HorizontalAdapter horizontalAdapter;
    VerticalAdapter verticalAdapter;
    List<Popular> popularList;
    List<Datum> dataList;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    private ApiInterface apiInterface;


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

        relativeLayout = (RelativeLayout) view.findViewById(R.id.activity_main);

        recyclerViewHorizontal = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        recyclerViewVertical = (RecyclerView) view.findViewById(R.id.vertical_recycler_view);
        recyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        popularList = Collections.<Popular>emptyList();
        dataList = Collections.<Datum>emptyList();
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        if (NetworkCheckingClass.isNetworkAvailable(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            fetchData();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
        }



        return view;
    }



    private void fetchData() {

        Call<JsonData> call = apiInterface.apiCall();
        call.enqueue(new Callback<JsonData>() {
            @Override
            public void onResponse(Call<JsonData> call, Response<JsonData> response) {

                JsonData jsonData = response.body();

                popularList = jsonData.getPopular();
                dataList = jsonData.getData();

                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

                //for spacing after every item
                if (popularList.size() > 0)
                    recyclerViewHorizontal.addItemDecoration(new GridSpacingItemDecoration(popularList.size(), spacingInPixels, true, 0));

                progressBar.setVisibility(View.GONE);

                relativeLayout.setBackgroundColor(Color.parseColor("#ffffb3"));


                horizontalAdapter = new HorizontalAdapter(getContext(), popularList);
                recyclerViewHorizontal.setAdapter(horizontalAdapter);
                verticalAdapter = new VerticalAdapter(getContext(), dataList);
                recyclerViewVertical.setAdapter(verticalAdapter);
            }

            @Override
            public void onFailure(Call<JsonData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
