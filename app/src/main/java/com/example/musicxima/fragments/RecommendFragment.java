package com.example.musicxima.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicxima.R;
import com.example.musicxima.adapters.RecommendListAdapter;
import com.example.musicxima.base.BaseFragment;
import com.example.musicxima.interfaces.IRecommendCallback;
import com.example.musicxima.presenters.RecommendPresenter;
import com.example.musicxima.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendCallback, UILoader.onRetryClickListener {
    private static final String TAG = "RecommendFragment";
    private RecyclerView mRecommendList;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;
    private View mRootView;

    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, final ViewGroup container) {
        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup containner) {
                return createSuccessView(inflater,container);
            }
        };

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();//获取推荐列表
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        mUiLoader.setOnRetryClickListener(this);
        //返回View,给界面显示
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater inflater,ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_recommend, container,false);
        //RecyclerView的使用，1、找到控件
        mRecommendList = mRootView.findViewById(R.id.recomment_list);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendList.setLayoutManager(layoutManager);
        mRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);


            }
        });
        //3、设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendList.setAdapter(mRecommendListAdapter);
        return  mRootView;
    }



   /* private void upRecommandUI(List<Album> albumList) {
        //把数据设置给适配器，并且更新ui
        mRecommendListAdapter.setData(result);
    }*/

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容时，这个方法就会被调用（成功了）
        //数据回来后就是更新ui
        //把数据设置给适配器，并且更新ui
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIstatus.SUCCESS);

    }

    @Override
    public void onNetWorkError() {
        mUiLoader.updateStatus(UILoader.UIstatus.NETWORK_ERROR);

    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIstatus.EMPTY);

    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIstatus.LOADING);
    }

   /* @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏，
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void RetryClick() {
        //表示网络不佳的时候，用户点击了重试；重新获取即可
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }
}