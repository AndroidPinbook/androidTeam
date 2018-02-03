package uur.com.pinbook.RecyclerView.Interface;

import retrofit2.Call;
import retrofit2.http.GET;
import uur.com.pinbook.RecyclerView.Model.JsonData;

public interface ApiInterface {

    @GET("data.json")
    Call<JsonData> apiCall();

}

