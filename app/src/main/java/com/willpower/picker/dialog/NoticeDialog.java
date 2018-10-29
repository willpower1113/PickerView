package com.willpower.picker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.willpower.picker.R;

/**
 * Created by Administrator on 2018/10/26.
 */

public class NoticeDialog extends Dialog {

    TextView titleView;

    TextView contentView;

    ImageView closeView;

    LinearLayout layoutNotice,layoutEmpty;

    public NoticeDialog(@NonNull Context context) {
        this(context, R.style.custom_dialog_type);
    }

    public NoticeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected NoticeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context, R.style.custom_dialog_type);
    }

    private void initView() {
        setCancelable(true);
        View view = View.inflate(getContext(), R.layout.dialog_room_notice, null);
        titleView = view.findViewById(R.id.tv_notice_title);
        contentView = view.findViewById(R.id.tv_notice_content);
        closeView = view.findViewById(R.id.image_notice_close);
        layoutNotice = view.findViewById(R.id.layout_notice);
        layoutEmpty = view.findViewById(R.id.layout_no_notice);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(view, new ViewGroup.LayoutParams((int) (getScreenWidth(getContext()) * 0.8f), getScreenWidth(getContext())));
    }


    private static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setContentText(String content) {
        if (contentView != null) {
            if (TextUtils.isEmpty(content)){
                layoutNotice.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }else {
                contentView.setText("\t\t" + content);
                layoutNotice.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        }
    }

}
