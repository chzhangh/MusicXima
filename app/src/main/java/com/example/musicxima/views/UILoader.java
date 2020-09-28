package com.example.musicxima.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicxima.R;
import com.example.musicxima.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetWorkErrorView;
    private View mEmptyView;
    private onRetryClickListener mOnRetryClickListener=null;

    public enum UIstatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIstatus mCurrentStatus = UIstatus.NONE;
    public UILoader(@NonNull Context context) {
        super(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        init();
    }

    public void updateStatus(UIstatus uIstatus){
        mCurrentStatus = uIstatus;
        //更新ui一定要在主线程上
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLodingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus == UIstatus.LOADING?VISIBLE:INVISIBLE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus == UIstatus.SUCCESS?VISIBLE:INVISIBLE);

        //网络错误页面
        if (mNetWorkErrorView == null) {
            mNetWorkErrorView = getNetWorkErrorView();
            addView(mNetWorkErrorView);
        }
        //根据状态设置是否可见
        mNetWorkErrorView.setVisibility(mCurrentStatus == UIstatus.NETWORK_ERROR?VISIBLE:INVISIBLE);

        //数据为空的界面
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus == UIstatus.EMPTY?VISIBLE:INVISIBLE);

    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty,this,false);
    }

    private View getNetWorkErrorView() {
        View netWorkError = LayoutInflater.from(getContext()).inflate(R.layout.fragment_error,this,false);
        netWorkError.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               //重新获取数据
               if (mOnRetryClickListener != null) {
                   mOnRetryClickListener.RetryClick();
               }
           }
       });
        return netWorkError;

    }

    protected abstract View getSuccessView(ViewGroup containner);

    private View getLodingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading,this,false);

    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setOnRetryClickListener(onRetryClickListener listener){
        this.mOnRetryClickListener = listener;
    }
    public interface onRetryClickListener{
        void RetryClick();
    }

}
