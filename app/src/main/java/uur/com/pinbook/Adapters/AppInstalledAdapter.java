package uur.com.pinbook.Adapters;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;


public class AppInstalledAdapter extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //Put the package name here...
        boolean installed = appInstalledOrNot("com.whatsapp");
        if(installed) {
            System.out.println("App is already installed on your phone");
        } else {
            System.out.println("App is not currently installed on your phone");
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
