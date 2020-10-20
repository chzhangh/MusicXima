package com.example.musicxima.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.R;
import com.example.musicxima.adapters.PlayListAdapter;
import com.example.musicxima.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mPlayListRv;
    private PlayListAdapter mPlayListAdapter;
    private PlayListItemClickListener mPlayListItemClickListener;
    private TextView mPlayModeTv;
    private ImageView mPlayModeImg;
    private View mPlayModeContainer;
    private PlayListActionClickListner mPlayListPlayModeClickListner = null;
    private View mOrderBtnContainer;
    private ImageView mOrderPlayList;
    private TextView mOrderText;

    public SobPopWindow() {
        //设置他的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //注意：有的设置的时候需要先设置,否则点击外部无法关闭pop
        //setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //加载进来的VIEW:
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和弹出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initEvent() {
        /**
         * 点击关闭消失
         */
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:切换播放模式

                if (mPlayListPlayModeClickListner != null) {
                    mPlayListPlayModeClickListner.onPlayListModeClick();
                }
            }
        });

        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表为顺序或者逆序
                mPlayListPlayModeClickListner.onOrderClick();

            }
        });
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_bg);
        mPlayListRv = mPopView.findViewById(R.id.play_list_rv);
        //设置布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mPlayListRv.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mPlayListRv.setAdapter(mPlayListAdapter);

        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeImg = mPopView.findViewById(R.id.play_list_play_mode_img);
        mPlayModeContainer = mPopView.findViewById(R.id.play_mode_control_container);

        //播放顺序
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderPlayList = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }


    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPosition(int position){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
        }
        mPlayListRv.scrollToPosition(position);
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListner(listener);
    }

    /**
     * 更新播放列表的播放模式
     * @param currentPlayMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentPlayMode) {
        upDatePlayModeBtnImg(currentPlayMode);

    }
    public void updateOrderIcon(boolean isOrder){

        mOrderPlayList.setImageResource(isOrder?R.drawable.selector_player_mode_list_order:R.drawable.selector_player_mode_list_reverse);
        mOrderText.setText(isOrder?R.string.play_mode_order:R.string.play_mode_reverse);
    }

    public interface PlayListItemClickListener {
        void onPlayListItemClick(int position);
    }

    public void setPlayListActionClickListner(PlayListActionClickListner listner){
        mPlayListPlayModeClickListner = listner;
    }

    public interface PlayListActionClickListner{
        //播放模式被点击了
        void onPlayListModeClick();
        //播放顺序被点击了
        void onOrderClick();
    }

    /**
     * 更改切换模式之后的图标
     */

    private void upDatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resID = R.drawable.selector_player_mode_list_order;
        int textid = R.string.play_mode_order;

        switch (playMode){
            case PLAY_MODEL_LIST:
                resID = R.drawable.selector_player_mode_list_order;
                textid = R.string.play_mode_order;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resID = R.drawable.selector_player_mode_list_order_loop;
                textid = R.string.play_mode_order_loop;
                break;
            case PLAY_MODEL_RANDOM:
                resID = R.drawable.selector_player_mode_list_order_random;
                textid = R.string.play_mode_random_txt;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resID = R.drawable.selector_player_mode_list_single_loop;
                textid = R.string.play_mode_order_single_loop;
                break;
        }
//        mPlaySwicthBtn.setImageResource(resID);
         mPlayModeImg.setImageResource(resID);
         mPlayModeTv.setText(textid);
    }
}
