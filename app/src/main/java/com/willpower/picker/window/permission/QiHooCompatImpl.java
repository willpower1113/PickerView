package com.willpower.picker.window.permission;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *  360 悬浮窗权限兼容实现
 *
 * Created by linchaolong on 2016/12/26.
 */
public class QiHooCompatImpl extends BelowApi23CompatImpl {

  private static final String TAG = "QiHooCompatImpl";

  @Override public boolean isSupported() {
    return true;
  }

  @Override public boolean apply(Context context) {
    Intent intent = new Intent();
    intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Utils.isIntentAvailable(context, intent)) {
      context.startActivity(intent);
      return true;
    } else {
      intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.appEnterActivity");
      if (Utils.isIntentAvailable(context, intent)) {
        context.startActivity(intent);
        return true;
      } else {
        Log.e(TAG, "can't open permission page with particular name, please use " +
            "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
      }
    }
    return false;
  }

}
