package uur.com.pinbook.Controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import uur.com.pinbook.R;

/**
 * Created by ASUS on 22.12.2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, View.OnClickListener{

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){


    }


    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {

        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public void onClick(View v) {
        Log.i("info ", "btn1 clicked..");
    }
}
