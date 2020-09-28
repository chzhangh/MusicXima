package com.example.musicxima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.musicxima.adapters.IndicatorAdapter;
import com.example.musicxima.adapters.MainContentAdapter;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mViewPager;
    private CommonNavigator commonNavigator;
    private MainContentAdapter mainContentAdapter;
    private IndicatorAdapter mIndicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvent();
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
        }


    }
}