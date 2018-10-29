package com.willpower.picker;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018/10/27.
 */

public class Demo1 extends AppCompatActivity implements View.OnClickListener {

    ImageView imageStart, imageEnd;

    String TAG = "动画";

    ImageView imageView;

    FrameLayout rootView;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        imageStart = findViewById(R.id.image_start);
        imageEnd = findViewById(R.id.image_end);
        rootView = findViewById(android.R.id.content);
        imageStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_start:
                cloneAndAnim();
                break;
            case R.id.image_end:
                break;
        }
    }

    private void cloneAndAnim() {
        if (imageView != null) {
            rootView.removeView(imageView);
            imageView = null;
        }
        Rect rounds = new Rect();
        imageStart.getGlobalVisibleRect(rounds);
        Log.e(TAG, "left: " + rounds.left + "top:" + rounds.top + "right:" + rounds.right + "bottom:" + rounds.bottom);
        imageView = new ImageView(this);
        imageView.setImageResource(getDrawable());
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
        ViewGroup.LayoutParams location = new ViewGroup.LayoutParams(rounds.width(), rounds.height());
        imageView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
        rootView.addView(imageView, location);
        anim(imageView);
    }


    static int[] images = {R.drawable.gift_1, R.drawable.gift_2, R.drawable.gift_3, R.drawable.gift_4, R.drawable.gift_5, R.drawable.gift_6, R.drawable.gift_7, R.drawable.gift_8};

    int getDrawable() {
        return images[(int) (Math.random() * images.length)];
    }


    private Bitmap copy(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    private void anim(View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", view.getLeft(), (getScreenWidth(this) - view.getWidth()) / 2);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", view.getTop(), (getScreenHeight(this) - view.getHeight()) / 2);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX, translationY);
        set.setDuration(1000);
        set.start();
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
