package com.willpower.picker;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Simple
 *
 * pickerView = findViewById(R.id.pickerView);
 * pickerView.setFocusableChangeView(view);
 * pickerView.setPlusDrawable(R.drawable.ic_plus,R.drawable.ic_plus_disable);
 * pickerView.setReduceDrawable(R.drawable.ic_reduce,R.drawable.ic_reduce_disable);
 * pickerView.setNumberDrawable(R.drawable.selector_edit_num);
 *
 * Created by Administrator on 2018/10/24.
 * 购物车选择数量控件
 */

public class PickerView extends LinearLayout {

    final String TAG = getClass().getSimpleName();

    ImageView reduceView, plusView;

    EditText numberView;

    int width, height;

    int marginView, paddingView;

    private long minNum, maxNum;//文本框支持的最大值和最小值

    /*drawable*/
    private int reduceNormalIcon, plusNormalIcon, numberIcon;//正常图标

    private int reduceDisableIcon, plusDisableIcon;//禁用图标

    public PickerView(Context context) {
        super(context);
        initView();
    }

    public PickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*初始化数据*/
    private void init() {
        minNum = 1;
        maxNum = -1;
        marginView = dp2px(getContext(), 10);
        paddingView = dp2px(getContext(), 10);
        reduceNormalIcon = 0;
        plusNormalIcon = 0;
        numberIcon = 0;
        initView();
    }

    /**/
    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);//水平排布
        /*
        添加布局
         */
        View view = View.inflate(getContext(), R.layout.view_picker, this);
        requestLayout();
        reduceView = view.findViewById(R.id.reduceView);
        plusView = view.findViewById(R.id.plusView);
        numberView = view.findViewById(R.id.numberView);

//        //设置图标背景
//        reduceView.setImageResource(reduceNormalIcon);
//        plusView.setImageResource(plusNormalIcon);
//        numberView.setBackgroundResource(numberIcon);

        numberView.setCursorVisible(false);//默认不显示光标
        numberView.setText(String.valueOf(minNum));
        reduceView.setImageResource(reduceDisableIcon == 0 ? reduceNormalIcon : reduceDisableIcon);

        //设置事件
        plusView.setOnClickListener(plusClick);
        reduceView.setOnClickListener(reduceClick);
        numberView.setOnFocusChangeListener(onFocusChangeListener);
        numberView.setOnTouchListener(onTouchListener);
        numberView.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = widthMeasureSpec;
        height = heightMeasureSpec;
        initChildLayout();
    }

    /*
     设置子控件大小
      */
    private void initChildLayout() {
        //减号
        LinearLayout.LayoutParams reduceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        reduceParams.width = reduceParams.height = height; /*固定宽高1:1*/
        reduceView.setLayoutParams(reduceParams);
        //加号
        ViewGroup.LayoutParams plusParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        plusParams.width = plusParams.height = height; /*固定宽高1:1*/
        plusView.setLayoutParams(plusParams);
        //文本框
        LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
        numberParams.height = height;
        //设置间距
        numberParams.setMargins(marginView, 0, marginView, 0);
        numberView.setPadding(paddingView, 0, paddingView, 0);
        numberView.setLayoutParams(numberParams);
    }

    /*
     隐藏软键盘,抢走焦点
     */
    private void hideSoftInput(View v) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*
    点击动画
     */
    private void clickAnim(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(700);
        set.playTogether(scaleX, scaleY);
        set.start();
    }


    /*
    监听数字变化
    */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            numberView.requestLayout();//每次改变数值后重新设置宽高
            if (getPickerNumber() <= minNum) {
                reduceView.setImageResource(reduceDisableIcon == 0 ? reduceNormalIcon : reduceDisableIcon);
            } else {
                reduceView.setImageResource(reduceNormalIcon);
            }
            if (maxNum != -1 && getPickerNumber() >= maxNum) {
                plusView.setImageResource(plusDisableIcon == 0 ? plusNormalIcon : plusDisableIcon);
            } else {
                plusView.setImageResource(plusNormalIcon);
            }
        }
    };

    /*
     编辑框的光标监听
     */
    OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (TextUtils.isEmpty(numberView.getText())) {
                    numberView.setText("1");
                }
                numberView.setCursorVisible(false);//隐藏光标
            }
        }
    };

    /*
    文本框的Touch事件
     */
    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            numberView.setCursorVisible(true);//显示光标
            return false;
        }
    };

    /*
    加号点击事件
     */
    OnClickListener plusClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (maxNum != -1 && getPickerNumber() >= maxNum) return;
            clickAnim(v);
            numberView.setText(String.valueOf(getPickerNumber() + 1));
        }
    };

    /*
   减号点击事件
    */
    OnClickListener reduceClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getPickerNumber() <= minNum) return;
            clickAnim(v);
            numberView.setText(String.valueOf(getPickerNumber() - 1));
        }
    };

    /*****************************************************************************************************************************
     * 对外提供方法
     */
    /*选择的数量*/
    public long getPickerNumber() {
        try {
            return Long.valueOf(numberView.getText().toString());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /*
    设置编辑框背景
     */
    public void setNumberDrawable(int drawable) {
        if (drawable == 0) return;
        numberView.setBackgroundResource(drawable);
    }

    /*设置减号图片*/
    public void setReduceDrawable(int normal, int disable) {
        if (normal == 0 || disable == 0) return;
        this.reduceNormalIcon = normal;
        this.reduceDisableIcon = disable;
        if (getPickerNumber() <= minNum) {
            reduceView.setImageResource(reduceDisableIcon);
        } else {
            reduceView.setImageResource(reduceNormalIcon);
        }
    }

    public void setReduceDrawable(int normal) {
        if (normal == 0) return;
        this.reduceNormalIcon = normal;
        reduceView.setImageResource(normal);
    }

    /*设置加号图片*/
    public void setPlusDrawable(int normal, int disable) {
        if (normal == 0 || disable == 0) return;
        this.plusNormalIcon = normal;
        this.plusDisableIcon = disable;
        if (maxNum != -1 && getPickerNumber() >= maxNum) {
            plusView.setImageResource(plusDisableIcon);
        } else {
            plusView.setImageResource(plusNormalIcon);
        }
    }

    public void setPlusDrawable(int normal) {
        if (normal == 0) return;
        this.plusNormalIcon = normal;
        plusView.setImageResource(normal);
    }


    /*设置点击外部隐藏光标*/
    public void setFocusableChangeView(final View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (numberView.hasFocus()) {
                    hideSoftInput(v);
                    v.requestFocus();
                }
                return false;
            }
        });
    }

    /*
    外部直接设置数量
     */
    public void setNumPicker(long num) {
        if (num <= minNum) return;
        if (maxNum != -1 && num >= maxNum) return;
        numberView.setText(String.valueOf(num));
    }

    /*设置支持最小数量*/
    public void setMinNum(long minNum) {
        this.minNum = minNum;
    }

    /*设置支持最大数量*/
    public void setMaxNum(long maxNum) {
        this.maxNum = maxNum;
    }

    /*
      对外提供控件
     */
    public ImageView getReduceView() {
        return reduceView;
    }

    public ImageView getPlusView() {
        return plusView;
    }

    public EditText getNumberView() {
        return numberView;
    }
}
