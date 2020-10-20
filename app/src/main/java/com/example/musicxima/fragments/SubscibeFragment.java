package com.example.musicxima.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.DetailActivity;
import com.example.musicxima.R;
import com.example.musicxima.adapters.AlbumListAdapter;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.base.BaseFragment;
import com.example.musicxima.interfaces.ISearchCallBack;
import com.example.musicxima.interfaces.ISubscriptionCallback;
import com.example.musicxima.interfaces.ISubscriptionPresenter;
import com.example.musicxima.presenters.AlbumDetailPresenter;
import com.example.musicxima.presenters.SearchPresenter;
import com.example.musicxima.presenters.SubscriptionPresenter;
import com.example.musicxima.views.ConfirmDialog;
import com.example.musicxima.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Collections;
import java.util.List;

public class SubscibeFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnClickItemListner, AlbumListAdapter.onAlbumItemLongClikListener, ConfirmDialog.onItemListener {
    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubList;
    private AlbumListAdapter mAlbumListAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private ConfirmDialog mDialog;
    private Album mCurrentAlbulm;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, final ViewGroup container) {
        View v =  inflater.inflate(R.layout.fragment_subscibe,container, false);
       /* if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup containner) {
                    return createSuccess(container);
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            v.addView(mUiLoader);
        }*/
        mSubList = v.findViewById(R.id.sub_list);
        mRefreshLayout = v.findViewById(R.id.over_scroll_view);
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setEnableRefresh(false);
        mSubList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setOnClickItemListner(this);
        mAlbumListAdapter.setOnAlbumItemLongClikListener(this);
        mSubList.setAdapter(mAlbumListAdapter);
        mSubList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);


            }
        });
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();

        return v;
    }

    private View createSuccess(ViewGroup container) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_subscription, null);

        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //给出取消订阅的提示：
        Toast.makeText(BaseApplication.getAppContext(), isSuccess?R.string.cancel_sub_success:R.string.cancel_fail, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
      /*if (albums.size()== 0) {
            mUiLoader.updateStatus(UILoader.UIstatus.EMPTY);
        }else {
            mUiLoader.updateStatus(UILoader.UIstatus.SUCCESS);
        }*/

        //更新ui
        if (mAlbumListAdapter != null) {
            //实现逆序排列
          //  Collections.reverse(albums);
            mAlbumListAdapter.setData(albums);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        mAlbumListAdapter.setOnClickItemListner(null);
    }

    @Override
    public void onSubTooMany() {
        Toast.makeText(BaseApplication.getAppContext(), "订阅太多内容了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clickItemListener(int position, Album album) {
        //根据位置拿到数据
        // item被点击了
        AlbumDetailPresenter.getInstance().setTagAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int clickPosition, Album album) {
        this.mCurrentAlbulm = album;
        //Toast.makeText(BaseApplication.getAppContext(), "订阅被长按了!", Toast.LENGTH_SHORT).show();
        mDialog = new ConfirmDialog(getActivity());
        mDialog.setOnItemListener(this);
        mDialog.show();
    }

    @Override
    public void onCancelClick() {
        //取消订阅，
        if (mCurrentAlbulm != null && mSubscriptionPresenter != null) {
            mSubscriptionPresenter.deleteSubscription(mCurrentAlbulm);
        }

    }

    @Override
    public void onGiveUpClick() {
      //我在想想
        mDialog.dismiss();

    }
}