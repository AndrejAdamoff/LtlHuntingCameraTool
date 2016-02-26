package com.adamoff.andrej.ltlhuntingcameratool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceRestartReceiver extends BroadcastReceiver {
    public ServiceRestartReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
    //    Log.e(TAG, "onReceive");
    //    context.startService(new Intent(context.getApplicationContext(), StartContentObserverService.class).putExtra("action","start").putExtra("who","alarm"));

         context.startService(new Intent(context, IMAPListener.class).putExtra("action", "startidleboot"));

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
