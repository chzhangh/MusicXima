package com.example.musicxima.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicxima.R;

public class ConfirmDialog extends Dialog {

    private TextView mGiveUpBtn;
    private TextView mCancelSub;
    private onItemListener mClickListner = null;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mGiveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListner.onGiveUpClick();
                dismiss();
            }
        });

        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListner.onCancelClick();
                dismiss();
            }
        });
    }

    private void initView() {
        mGiveUpBtn = findViewById(R.id.giveup_tv);
        mCancelSub = findViewById(R.id.cancel_sub);
    }

    public void setOnItemListener(onItemListener listener){
        this.mClickListner = listener;
    }
    public interface onItemListener{
        void onCancelClick();
        void onGiveUpClick();
    }
}
