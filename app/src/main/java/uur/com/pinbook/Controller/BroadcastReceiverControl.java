package uur.com.pinbook.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverControl extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch(action) {
            case "do_something":
                doSomething();
                break;
        }
    }

    private void doSomething() {

        Log.i("INFO", "doSomething func started !!");
    }
}