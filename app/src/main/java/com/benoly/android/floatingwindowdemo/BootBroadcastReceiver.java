package com.benoly.android.floatingwindowdemo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = "BootReceiver_Tag";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive");
    context.startService(new Intent(context, FloatingWindowService.class));
  }
}
