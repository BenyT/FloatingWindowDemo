package com.benoly.android.floatingwindowdemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

  private Button btnStart, btnStop;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initView();
    tryAddAdminActiveForlockScreen();
  }

  private void initView() {
    btnStart = (Button) findViewById(R.id.btnStart);
    btnStop = (Button) findViewById(R.id.btnStop);
  }

  public void onStart(View view) {
    startService(new Intent(MainActivity.this, FloatingWindowService.class));
  }

  public void onStop(View view) {
    stopService(new Intent(MainActivity.this, FloatingWindowService.class));
  }

  /**
   * 安装之初就提示用户激活设备管理权限
   */
  private void tryAddAdminActiveForlockScreen() {
    Log.d("main_activity_tag", "lockScream");

    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
    ComponentName componentName = new ComponentName(this, FloatingWindowDeviceAdminReceiver.class);

    if (!devicePolicyManager.isAdminActive(componentName)) {
      Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能");

      startActivity(intent);
    }
  }
}
