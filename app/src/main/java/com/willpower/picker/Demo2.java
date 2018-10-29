package com.willpower.picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.willpower.picker.anim.GiftAnimHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/27.
 */

public class Demo2 extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;

    ImageView imageView;

    FrameLayout rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        recyclerView = findViewById(R.id.mRecyclerView);
        rootView = findViewById(android.R.id.content);
        adapter = new MyAdapter(getData());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                List<View> targetViews = new ArrayList<>();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    targetViews.add(recyclerView.getChildAt(i));
                }
                GiftAnimHelper.createInstance(Demo2.this, getDrawable(), rootView, targetViews).start(view);
            }
        });
    }


    private void cloneAndAnim(View view) {
        if (imageView != null) {
            rootView.removeView(imageView);
            imageView = null;
        }
        Rect rounds = new Rect();
        view.getGlobalVisibleRect(rounds);
        imageView = new ImageView(this);
        imageView.setImageDrawable(getDrawable());
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
        ViewGroup.LayoutParams location = new ViewGroup.LayoutParams(rounds.width(), rounds.height());
        rootView.addView(imageView, location);
        anim(imageView);
    }


    static int[] images = {R.drawable.gift_1, R.drawable.gift_2, R.drawable.gift_3, R.drawable.gift_4, R.drawable.gift_5, R.drawable.gift_6, R.drawable.gift_7, R.drawable.gift_8};

    Drawable getDrawable() {
        return getResources().getDrawable(images[(int) (Math.random() * images.length)]);
    }


    private void anim(View view) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", view.getLeft(), (getScreenWidth(this) - view.getWidth()) / 2);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", view.getTop(), (getScreenHeight(this) - view.getHeight()) / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 2);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 2);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX, translationY, scaleX, scaleY);
        set.setDuration(1000);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                splitView(imageView);
            }
        });
        set.start();
    }

    List<ImageView> imageViews = new ArrayList<>();

    List<View> itemViews = new ArrayList<>();

    private void splitView(ImageView view) {
        imageViews.clear();
        itemViews.clear();
        Rect rounds = new Rect();
        view.getGlobalVisibleRect(rounds);
        Drawable drawable = view.getDrawable();
        rootView.removeView(view);
        imageView = null;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            itemViews.add(recyclerView.getChildAt(i));
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(drawable);
            imageView.layout(rounds.left, rounds.top, rounds.right, rounds.bottom);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(rounds.width(), rounds.height());
            rootView.addView(imageView, params);
            imageViews.add(imageView);
        }
        splitAnim(itemViews, imageViews);
    }

    /*
    分裂动画
     */
    private void splitAnim(List<View> views, List<ImageView> animViews) {
        if (views.size() != animViews.size()) return;
        for (int i = 0; i < animViews.size(); i++) {
            anim2(animViews.get(i), views.get(i));
        }
    }

    private void anim2(View startView, View endView) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(startView, "translationX", startView.getLeft(), endView.getLeft() - endView.getWidth() / 2);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(startView, "translationY", startView.getTop(), endView.getTop());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(startView, "scaleX", 1, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(startView, "scaleY", 1, 0.5f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX, translationY, scaleX, scaleY);
        set.setDuration(1000);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < imageViews.size(); i++) {
                            rootView.removeView(imageViews.get(i));
                        }
                    }
                }, 500);
            }
        });
        set.start();
    }

    List<String> getData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(i + "");
        }
        return list;
    }


    static class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter(@Nullable List<String> data) {
            super(R.layout.item_demo2, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            ImageView imageView = helper.getView(R.id.headerView);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = getScreenWidth(mContext) / 4 - 10;
            imageView.setLayoutParams(params);
            helper.addOnClickListener(R.id.headerView);
        }
    }

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
