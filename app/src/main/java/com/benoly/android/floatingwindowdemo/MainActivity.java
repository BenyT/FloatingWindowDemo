package com.benoly.android.floatingwindowdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

  private Button btnStart, btnStop;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initView();
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
}
