package com.willpower.picker.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/10/27.
 * 礼物动画辅助类
 */
public class GiftAnimHelper {
    //分散后执行动画的View集合
    private List<ImageView> animViews;
    //一开始做动画的View
    private ImageView giftView;
    //目标View集合
    private List<View> targetViews;

    private ViewGroup rootView;//根布局

    private Context context;

    private Drawable drawable;

    private int targetNumber;

    private long duration = 1000;

    /**
     * 入口
     */
    public static GiftAnimHelper createInstance(Context context, Drawable drawable, ViewGroup rootView, List<View> targetViews) {
        return new GiftAnimHelper(context, drawable, targetViews, rootView);
    }

    public static GiftAnimHelper createInstance(Context context, Drawable drawable, ViewGroup rootView, View... targetView) {
        return new GiftAnimHelper(context, drawable, Arrays.asList(targetView), rootView);
    }

    //自动释放资源
    public void start(View view) {
        start(view, defaultListener);
    }

    //带结束监听，需要手动调用clear释放资源
    public void start(View view, OnDestroyListener destroyListener) {
        this.destroyListener = destroyListener;
        if (giftView != null) {
            rootView.removeView(giftView);
            giftView = null;
        }
        Rect rounds = new Rect();
        view.getGlobalVisibleRect(rounds);
        giftView = new ImageView(context);
        giftView.setImageDrawable(drawable);
        giftView.setBackgroundColor(Color.TRANSPARENT);
        giftView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
        ViewGroup.LayoutParams location = new ViewGroup.LayoutParams(rounds.width(), rounds.height());
        rootView.addView(giftView, location);
        firstAnim();
    }

    /**
     * @param context
     * @param drawable    -- 礼物图片
     * @param targetViews -- 目标View集合
     * @param rootView    -- 根布局，用于添加View
     */
    private GiftAnimHelper(Context context, Drawable drawable, List<View> targetViews, ViewGroup rootView) {
        this.context = context;
        this.drawable = drawable;
        this.targetViews = new ArrayList<>();
        for (int i = 0; i < targetViews.size(); i++) {
            if (targetViews.get(i) != null) {//超出屏幕的item为null，这里去除
                this.targetViews.add(targetViews.get(i));
            }
        }
        this.rootView = rootView;
        this.targetNumber = this.targetViews.size();
        this.animViews = new ArrayList<>();
    }

    //-------------------------------第一阶段动画-----------------------------------------

    /*
    复制View
     */
    /*
    放大，位移到屏幕中心
     */
    private void firstAnim() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(giftView, "translationX",
                giftView.getLeft(), (getScreenWidth(context) - giftView.getWidth()) / 2);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(giftView, "translationY",
                giftView.getTop(), (getScreenHeight(context) - giftView.getHeight()) / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(giftView, "scaleX", 1, 2);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(giftView, "scaleY", 1, 2);

        //到屏幕中间后的弹簧动画
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(giftView, "scaleX", 2, 1.9f, 2, 1.9f, 2);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(giftView, "scaleY", 2, 1.9f, 2, 1.9f, 2);
        final AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(scaleX2, scaleY2);
        set2.setDuration(duration);
        set2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                splitView();//动画结束开始复制，分散
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX, translationY, scaleX, scaleY);
        set.setDuration(duration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set2.start();
            }
        });
        set.start();
    }

    /*
    分裂效果，复制出多个同样的View
     */
    private void splitView() {
        Rect rounds = new Rect();
        giftView.getGlobalVisibleRect(rounds);
        Drawable drawable = giftView.getDrawable();
        rootView.removeView(giftView);
        giftView = null;
        if (targetNumber == 0) {//没有目标View
            destroyListener.onDestroy();
            return;
        }
        for (int i = 0; i < targetNumber; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(drawable);
            imageView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(rounds.width(), rounds.height());
            rootView.addView(imageView, params);
            animViews.add(imageView);
        }
        splitAnim();
    }


    //-------------------------------第二阶段动画-----------------------------------------

    /**
     * 将各个View移动缩小到指定位置
     * <p>
     * targetViews     --目标View集合
     * animViews -- 做动画的View集合
     */

    private void splitAnim() {
        if (targetViews.size() != animViews.size()) return;
        for (int i = 0; i < animViews.size(); i++) {
            secondAnim(animViews.get(i), targetViews.get(i), i);
        }

    }

    private void secondAnim(final View startView, View endView, final int count) {
        if (endView == null) return;

        ObjectAnimator translationX = ObjectAnimator.ofFloat(startView, "translationX",
                startView.getLeft(), endView.getLeft() - (startView.getWidth() / 4));
        ObjectAnimator translationY = ObjectAnimator.ofFloat(startView,
                "translationY", startView.getTop(), endView.getTop());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(startView, "scaleX", 1, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(startView, "scaleY", 1, 0.5f);

        //结尾弹簧动画
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(startView, "scaleX", 0.4f, 0.5f, 0.4f, 0.5f);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(startView, "scaleY", 0.4f, 0.5f, 0.4f, 0.5f);

        final AnimatorSet set2 = new AnimatorSet();
        set2.setDuration(duration);
        set2.playTogether(scaleX2, scaleY2);
        set2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (count == (animViews.size() - 1)) {
                    destroyListener.onDestroy();
                }
            }
        });
        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(translationX, translationY, scaleX, scaleY);
        set1.setDuration(duration);
        set1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set2.start();
            }
        });
        set1.start();


    }

    /*
    释放资源 -- 动画执行完成前不可调用
    */
    public void clear() {
        if (animViews != null) {
            Log.e("动画", "清理View: ");
            for (int i = 0; i < targetNumber; i++) {
                rootView.removeView(animViews.get(i));
            }
            animViews.clear();
            animViews = null;
        }
        giftView = null;
        if (targetViews != null) {
            targetViews.clear();
            targetViews = null;
        }
        rootView = null;
        context = null;
        drawable = null;
    }


    OnDestroyListener destroyListener;

    OnDestroyListener defaultListener = new OnDestroyListener() {
        @Override
        public void onDestroy() {
            clear();
        }
    };

    public interface OnDestroyListener {
        void onDestroy();
    }

    //----------------------------------------------------Utils----------------------------------
    /*
    获取屏幕宽度
    */
    private static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
