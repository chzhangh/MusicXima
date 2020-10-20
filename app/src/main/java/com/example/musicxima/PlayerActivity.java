package com.example.musicxima;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicxima.adapters.PlayerTrackPagerAdapter;
import com.example.musicxima.base.BaseActivity;
import com.example.musicxima.interfaces.IPlayerCallBack;
import com.example.musicxima.presenters.PlayerPresenter;
import com.example.musicxima.utils.LogUtil;
import com.example.musicxima.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallBack, ViewPager.OnPageChangeListener {
    private static final String TAG = "PlayerActivity";
    private TextView mTrackTitleTv;
    private ViewPager mTrackPageView;
    private ImageView mControlBtn;
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private String mTrackTitleText;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;//用户是否滑动了页面
    private ImageView mPlaySwicthBtn;//切换播放模式按钮
    //处理播放模式的切换
    //1、默认的是：PLAY_MODE_LIST
    //2、链表循环:PLAY_MODE_LIST_LOOP
    //3、随机播放 ：PLAY_MODE_RANDOM
    //4\单曲循环:PLAY_MODE_SINGLE_LOOP
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModePlayModeMap = new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    static {
        sPlayModePlayModeMap.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModePlayModeMap.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModePlayModeMap.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModePlayModeMap.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayList;
    private SobPopWindow mMSobPopWindow;
    private ValueAnimator mEntor;
    private ValueAnimator mOutBgAnimator;
    private static  final int BG_ANIMATION_DURATION = 800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        //测试一下播放
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //mPlayerPresenter.getPlayList();//获取播放列表
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEntor = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEntor.setDuration(BG_ANIMATION_DURATION);
        mEntor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                LogUtil.d(TAG,"=============="+animation);
                //处理下背景，
                upDateBgAlpha(animatedValue);
            }
        });
        //退出的
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                //处理下背景，
                upDateBgAlpha(animatedValue);
            }
        });
    }

    /* *//**
     * 播放功能
     *//*
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }*/

    /**
     * 给控件设置相关的事件
     */

    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ruguo现在的状态是正在播放则暂停，反之，就让播放器播放
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                }else {
                    mPlayerPresenter.play();
                }
            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 mIsUserTouchProgressBar = true; //代表手没有离开过进度条
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 //手离开拖动更新进度条的时候更新进度
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);


            }
        });
        /**
         * 播放下一首
         */
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playNext();
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playPrevious();
            }
        });
        mTrackPageView.addOnPageChangeListener(this);
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                    break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
        mPlaySwicthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });
        /**
         * 展示播放列表
         */
        mPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);

                //修改背景的透明度有一个渐变的过程
                mEntor.start();

            }
        });
        mMSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimator.start();//窗体退出后，太透明度的变化
            }
        });

        mMSobPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onPlayListItemClick(int position) {
                //说明列表里面的item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mMSobPopWindow.setPlayListActionClickListner(new SobPopWindow.PlayListActionClickListner() {
            @Override
            public void onPlayListModeClick() {
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
              //点击了切换了顺序和逆序
                //Toast.makeText(PlayerActivity.this, "", Toast.LENGTH_SHORT).show();
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }

              /*  mMSobPopWindow.updateOrderIcon(!testOrder);
                testOrder = !testOrder;*/
            }
        });

    }
    private boolean testOrder = false;
    private void switchPlayMode() {
        //处理播放模式的切换
        //1、默认的是：PLAY_MODE_LIST
        //2、链表循环:PLAY_MODE_LIST_LOOP
        //3、随机播放 ：PLAY_MODE_RANDOM
        //4\单曲循环PLAY_MODE_SINGLE_LOOP
        //根据当前的mode，获取下一个mode，
        XmPlayListControl.PlayMode playMode = sPlayModePlayModeMap.get(mCurrentPlayMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void upDateBgAlpha(Float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 更改切换模式之后的图标
     */

    private void upDatePlayModeBtnImg() {
        int resID = R.drawable.selector_player_mode_list_order;

        switch (mCurrentPlayMode){
            case PLAY_MODEL_LIST:
                resID = R.drawable.selector_player_mode_list_order;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resID = R.drawable.selector_player_mode_list_order_loop;
                break;
            case PLAY_MODEL_RANDOM:
                resID = R.drawable.selector_player_mode_list_order_random;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resID = R.drawable.selector_player_mode_list_single_loop;
                break;
        }
        mPlaySwicthBtn.setImageResource(resID);
    }

    private void initView() {
        mTrackTitleTv = this.findViewById(R.id.play_title);
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mPlaySwicthBtn = this.findViewById(R.id.player_mode_switch_btn);//切换播放模式按钮
        mPlayList = this.findViewById(R.id.player_list);//播放列表
        mMSobPopWindow = new SobPopWindow();

        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = this.findViewById(R.id.play_cover);
        //创建适配器，设置适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackPageView.setAdapter(mTrackPagerAdapter);
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改ui层暂停的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_normal);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> trackList) {
        LogUtil.d(TAG,"trackList"+trackList);
        //把数据设置到适配器里面
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(trackList);
        }
        //数据回来以后，也要给播放列表一份
        //todo:
        if (mMSobPopWindow != null) {
            mMSobPopWindow.setListData(trackList);
        }
    }

    @Override
    public void onplayModeChanger(XmPlayListControl.PlayMode playMode) {
        //更新播放模式并且更新ui
        mCurrentPlayMode = playMode;
        //更新popwindow里面的播放模式
        mMSobPopWindow.updatePlayMode(mCurrentPlayMode);
        upDatePlayModeBtnImg();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onProgressChange(int currentProgress, int total) {
        mDurationBar.setMax(total);
        String totalTime;
        String currentPosition;
        //更新进度条
        if (total > 1000*60*60) {
            totalTime = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentProgress);
        }else {
            totalTime = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentProgress);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalTime);
        }
        //更新当前的时间
        if(mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新当前的进度
        //计算当前的进度
        if (!mIsUserTouchProgressBar) {
           /* int percent = (int) (currentProgress*1.0f/total*100);
            // LogUtil.d(TAG,"percent"+percent);*/
            mDurationBar.setProgress(currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int position) {
        if (track == null) {
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候，我们就获取当前播放中播放位置
        //当节目修改以后，要修改页面的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(position,true);
        }
        //修改播放链表里面的播放位置
        if (mMSobPopWindow != null) {
            mMSobPopWindow.setCurrentPosition(position);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mMSobPopWindow.updateOrderIcon(isReverse);

    }


    @Override
    protected void onDestroy() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        super.onDestroy();
    }

    /**
     * viewPager的滑动监听，实现图片联动效果
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d(TAG,"----------------------------"+position);
        //当页面选中的时候就去切换播放的内容
        if (mPlayerPresenter != null &&mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}