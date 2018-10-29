package com.willpower.picker.window;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.willpower.picker.R;
import com.willpower.picker.window.permission.FloatingPermissionCompat;

/**
 * Created by Administrator on 2018/10/26.
 * 悬浮窗管理类
 */

public class FloatManager {

    private static FloatManager manager;

    private Application application;

    private WindowManager windowManager;

    private WindowManager.LayoutParams params;

    private LinearLayout touchLayout;

    private ImageView closeImage, headerView;//关闭，头像

    private TextView titleView, ownerView;//标题，房主

    private boolean showWindow = false;

    private boolean created = false;

    private boolean move;

    private long touchTimeMillis = 0;

    /*滑动相关*/
    private float translationX;
    private float translationY;
    private float downX;
    private float downY;
    private int slop;

    private FloatManager() {
    }

    public static FloatManager getManager() {
        if (manager == null) {
            manager = new FloatManager();
        }
        return manager;
    }

    public void init(Application application) {
        this.application = application;
    }

    private void createWindow() {
        if (!checkPermission(application)) {
            return;
        }
        created = true;
        slop = 1;
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        //Android8.0行为变更，对8.0进行适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        //设置悬浮窗口长宽数据.
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(application);
        //获取浮动窗口视图所在布局.
        touchLayout = (LinearLayout) inflater.inflate(R.layout.view_float, null);
        closeImage = touchLayout.findViewById(R.id.image_window_close);
        headerView = touchLayout.findViewById(R.id.image_owner_header);
        titleView = touchLayout.findViewById(R.id.tv_window_title);
        ownerView = touchLayout.findViewById(R.id.tv_window_owner);
        //主动计算出当前View的宽高信息.
        touchLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        initListener();
    }

    /*设置控件事件*/
    private void initListener() {
        touchLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    touchLayout.setBackgroundResource(R.drawable.shape_float_light);
                    downX = event.getRawX();
                    downY = event.getRawY();
                    move = false;
                    touchTimeMillis = System.currentTimeMillis();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float moveX = event.getRawX() - downX;
                    float moveY = event.getRawY() - downY;

                    Log.e("悬浮窗", "moveX: " + moveX);
                    Log.e("悬浮窗", "moveY: " + moveY);

                    params.x = (int) (translationX + moveX);
                    params.y = (int) (translationY + moveY);
                    windowManager.updateViewLayout(touchLayout, params);
                    if (Math.abs(moveX) > slop || Math.abs(moveY) > slop) {
                        move = true;
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    touchLayout.setBackgroundResource(R.drawable.shape_float_dark);
                    translationX = params.x;
                    translationY = params.y;
                    if (!move && (System.currentTimeMillis() - touchTimeMillis) > 100) {
                        if (clickListener != null) {
                            clickListener.onFloatWindowClick();
                        }
                    }
                }
                return true;
            }
        });

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWindow();
            }
        });
    }

    /*检查权限*/
    public boolean checkPermission(Context context) {
        if (context == null) return false;
        if (FloatingPermissionCompat.get().check(context)) {
            return true;
        }
        return false;
    }

    /*申请权限 Context -- @Activity */
    public void requestPermission(final Context context) {
        if (context == null) return;
        // 授权提示
        new AlertDialog.Builder(context)
                .setTitle("提醒")
                .setMessage("暂未开启悬浮窗权限，打开后才能使用小窗哦")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FloatingPermissionCompat.get().apply(context);
                    }
                }).setNegativeButton("取消", null).show();
    }

    /*******************************************************对外提供方法*********************************************/

    /*打开悬浮窗*/
    public void showWindow() {
        if (!checkPermission(application)) {
            return;
        }
        if (!created) {
            createWindow();
        }
        if (!showWindow && touchLayout != null) {
            showWindow = true;
//            translationX = getScreenWidth(application) - params.width;
//            translationY = getScreenHeight(application) - 200;
//            params.x = (int) translationX;
//            params.y = (int) translationX;

            translationX = (int) (getScreenWidth(application) * 0.4f);
            translationY = (int) (getScreenHeight(application) * 0.8f);

            params.x = (int) translationX;
            params.y = (int) translationY;

            Log.e("悬浮窗", "params.x: " + params.x);
            Log.e("悬浮窗", "params.y: " + params.y);
            Log.e("悬浮窗", "手机宽: " + getScreenWidth(application));
            Log.e("悬浮窗", "手机高: " + getScreenHeight(application));
            windowManager.addView(touchLayout, params);
        }
    }

    /*关闭悬浮窗*/
    public void hideWindow() {
        if (!checkPermission(application)) {
            return;
        }
        if (showWindow && touchLayout != null) {
            showWindow = false;
            windowManager.removeView(touchLayout);
        }
    }

    /**
     * 更新文字
     *
     * @param title
     * @param owner
     */
    public void updateText(String title, String owner) {
        if (!showWindow) return;
        if (titleView != null) {
            titleView.setText(title);
        }
        if (ownerView != null) {
            ownerView.setText(owner);
        }
    }

    /**
     * 更新头像
     *
     * @param url
     */
    public void updateHeader(String url) {
        if (!showWindow) return;
    }

    OnFloatWindowClickListener clickListener;

    public void setClickListener(OnFloatWindowClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnFloatWindowClickListener {
        void onFloatWindowClick();
    }


    /*
    获取屏幕宽度
     */
    private int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
