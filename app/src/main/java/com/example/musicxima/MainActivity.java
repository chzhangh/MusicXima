package com.example.musicxima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicxima.DataBase.XimalayaDBHelper;
import com.example.musicxima.adapters.IndicatorAdapter;
import com.example.musicxima.adapters.MainContentAdapter;
import com.example.musicxima.interfaces.IPlayerCallBack;
import com.example.musicxima.presenters.PlayerPresenter;
import com.example.musicxima.presenters.RecommendPresenter;
import com.example.musicxima.utils.LogUtil;
import com.example.musicxima.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements IPlayerCallBack {
    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mViewPager;
    private CommonNavigator commonNavigator;
    private MainContentAdapter mainContentAdapter;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlaycontrol;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvent();
        initPresenter();
        XimalayaDBHelper ximalayaDBHelper = new XimalayaDBHelper(this);
        ximalayaDBHelper.getWritableDatabase();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListner(new IndicatorAdapter.onIndicatorTabClickListner() {

            @Override
            public void onTabclick(int index) {
                LogUtil.d(TAG,"click index is" + index);
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });
        mPlaycontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有就设置播放列表，默认的是播放默认的第一个推荐专辑
                        //第一个推荐专辑每天都会变，
                        playFirstRecommend();

                    }else{

                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        }else{
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });

        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                //跳转到播放器界面
                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null) {
            Album album = currentRecommend.get(0);
            long id = album.getId();
            mPlayerPresenter.playByAlbumId(id);
        }
    }

    private void initViews() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mMagicIndicator.setBackgroundColor(this.getColor(R.color.main_color));
            //创建适配器
            mIndicatorAdapter = new IndicatorAdapter(this);
            commonNavigator = new CommonNavigator(this);
            commonNavigator.setAdjustMode(true); //自我调节评分
            commonNavigator.setAdapter(mIndicatorAdapter);


            //viewPager
            mViewPager = this.findViewById(R.id.content_pager);

            //
            FragmentManager fragmentManager = getSupportFragmentManager();
            mainContentAdapter = new MainContentAdapter(fragmentManager);
            mViewPager.setAdapter(mainContentAdapter);
            //把指示器与Viewpager绑定在一起
            mMagicIndicator.setNavigator(commonNavigator);
            ViewPagerHelper.bind(mMagicIndicator, mViewPager);
            //播放控制相关的
            mRoundRectImageView = this.findViewById(R.id.track_cover);
            mHeaderTitle = this.findViewById(R.id.main_head_title);
            mHeaderTitle.setSelected(true);//设置跑马灯
            mSubTitle = this.findViewById(R.id.main_sub_title);
            mPlaycontrol = findViewById(R.id.main_play_control);
            mPlayControlItem = findViewById(R.id.main_play_control_item);
            //搜索
            mSearchBtn = findViewById(R.id.search_btn);

        }


    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying){
        if (mPlaycontrol != null) {
            mPlaycontrol.setImageResource(isPlaying?R.drawable.selector_player_stop:R.drawable.selector_player_playorstop);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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

    }

    @Override
    public void onplayModeChanger(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int position) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            if (mRoundRectImageView != null) {
               Picasso.get().load(coverUrlMiddle).into(mRoundRectImageView);
            }

        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }
}