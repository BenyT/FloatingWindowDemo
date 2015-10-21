package com.benoly.android.floatingwindowdemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

public class FloatingWindowService extends Service {

  private static final String TAG = "FloatingWindowSVC_Tag";

  private WindowManager windowManager;
  private ImageView imgCircle;

  @Nullable
  @Override

  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind");
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate");

    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    imgCircle = new ImageView(this);
    imgCircle.setImageResource(R.drawable.circle);

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    {
      params.width = 128; //WindowManager.LayoutParams.WRAP_CONTENT;
      params.height = 128;// WindowManager.LayoutParams.WRAP_CONTENT;
      params.type = WindowManager.LayoutParams.TYPE_PHONE;
      params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
      params.format = PixelFormat.TRANSLUCENT;
      params.gravity = Gravity.TOP | Gravity.LEFT;
      params.x = 0;
      params.y = 0;
    }

    windowManager.addView(imgCircle, params);

    imgCircle.setOnTouchListener(new View.OnTouchListener() {
      private int x, y;
      private float ex, ey;
      private MotionEventTimer eventTimer;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            if (eventTimer == null || eventTimer.isFinished()) {
              eventTimer = new MotionEventTimer(1000, 100);
              eventTimer.start();
            }

            eventTimer.addEvent(event);

            x = params.x;
            y = params.y;
            ex = event.getRawX();
            ey = event.getRawY();

            break;
          case MotionEvent.ACTION_UP:
            //check click event
            if (Math.abs(x - params.x) <= 5 && Math.abs(y - params.y) <= 5) {
              Log.d(TAG, "onTouch up");

              eventTimer.addEvent(event);
              eventTimer.fireClickEvent();
            }

            break;
          case MotionEvent.ACTION_MOVE:
            params.x = x + (int) (event.getRawX() - ex);
            params.y = y + (int) (event.getRawY() - ey);

            windowManager.updateViewLayout(imgCircle, params);
            break;
        }

        return false;
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");

    if (imgCircle != null) {
      windowManager.removeView(imgCircle);
    }
  }

  /**
   * 返回主屏
   */
  private void returnHomeScreem() {
    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
    homeIntent.addCategory(Intent.CATEGORY_HOME);
    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    startActivity(homeIntent);
  }

  /**
   * 单击
   */
  private void onClick() {
    Log.d(TAG, "onClick");
  }

  /**
   * 双击
   */
  private void onDoubleClick() {
    Log.d(TAG, "onDoubleClick");
  }

  /**
   * 长按
   */
  private void onLongClick() {
    Log.d(TAG, "onLongClick");
  }

  private void onTriangleClick() {
    Log.d(TAG, "onTriangleClick");
  }

  private class MotionEventTimer extends CountDownTimer {

    private boolean finished = false;
    private Stack<String> eventHandlers = new Stack<>();
    private Stack<EventEntry> events = new Stack<>();

    public MotionEventTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
      Log.d(TAG, "onTick millisUntilFinished is " + millisUntilFinished);
    }

    @Override
    public void onFinish() {
      if (!eventHandlers.empty()) {
        String handler = eventHandlers.pop();

        if (handler.equals("onClick")) {
          EventEntry upEvent = events.pop();
          EventEntry downEvent = events.pop();

          //判断是否是长按
          Log.d(TAG, "diff is " + (upEvent.EventTime - downEvent.EventTime));

          if (upEvent.EventTime - downEvent.EventTime <= 300) {
            onClick();
          } else {
            onLongClick();
          }
        } else if (handler.equals("onDoubleClick")) {
          onDoubleClick();
        } else if (handler.equals("onTriangleClick")) {
          onTriangleClick();
        } else {
          //DO NOTHING
        }
      } else {
        //如果超时了，则认为是没有通过up action主动触发longClick，则手动触发longClick,此时只有一个down event在evnet stack中。
        if (events.size() == 1 && events.pop().Action == MotionEvent.ACTION_DOWN) {
          onLongClick();
        }
      }

      events.clear();
      eventHandlers.clear();
      finished = true;
    }

    public boolean isFinished() {
      return finished;
    }

    public void addEvent(MotionEvent event) {
      /**
       * TIP:不能直接把MotionEvent实例压入栈
       *
       * 因为在onTouch(View v, MotionEvent event)回调中，event是同一个对象，而不是一个新的event对象,
       * 所以,需要自行实例化自定义EventEntry类来保存MotionEvent的相关值，并压入栈
       *
       * deleted:
       * events.push(event);
       */
      events.push(new EventEntry(event.getAction(), event.getEventTime()));
    }

    public void fireClickEvent() {
      Log.d(TAG, "events size() is " + events.size());

      if (events.size() == 2) {
        eventHandlers.push("onClick");
      } else if (events.size() == 4) {
        eventHandlers.push("onDoubleClick");
      } else if (events.size() == 6) {
        eventHandlers.push("onTriangleClick");
      } else {
        //DO NOTHING
      }
    }

    private class EventEntry {

      public EventEntry(int action, long eventTime) {
        Action = action;
        EventTime = eventTime;
      }

      public int Action;
      public long EventTime;
    }
  }
}
