package com.example.musicxima;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicxima.adapters.AlbumListAdapter;
import com.example.musicxima.adapters.SearchRecommendAdapter;
import com.example.musicxima.base.BaseActivity;
import com.example.musicxima.interfaces.ISearchCallBack;
import com.example.musicxima.presenters.AlbumDetailPresenter;
import com.example.musicxima.presenters.SearchPresenter;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.example.musicxima.views.FlowTextLayout;
import com.example.musicxima.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallBack{
    private static final String TAG = "";
    private View mBackBtn;
    private EditText mInput;
    private TextView mSeachBtn;
    private FrameLayout mSearchContainer;
    private SearchPresenter mSearchPresenter;
    private UILoader mContent;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mInputMethodManager;
    private View mResultView;
    private InputMethodManager mInputMethodManager1;
    private View mDeleteBtn;
    private RecyclerView mSearchRecommend;
    private SearchRecommendAdapter mAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean isNeedSuggestion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initPresenter();
        initEvent();
    }

    private void initPresenter() {
        mInputMethodManager1 = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        //注册ui更新的接口
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
        //获取热词
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                LogUtil.d(TAG,"--------");
                //加载更多
                   if (mSearchPresenter != null) {
                       mSearchPresenter.loadMore();
                   }

            }
        });
        mAlbumListAdapter.setOnClickItemListner(new AlbumListAdapter.OnClickItemListner() {
            @Override
            public void clickItemListener(int position, Album album) {
                AlbumDetailPresenter.getInstance().setTagAlbum(album);
                Intent intent = new Intent(getBaseContext(),DetailActivity.class);
                startActivity(intent);
            }
        });
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInput.setText("");
            }
        });
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
                @Override
                public void onItemClick(String text) {
                    isNeedSuggestion = false;
                    searchSwith(text);
                }
            });

         mAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
             @Override
             public void onItemclick(String keyword) {
                 Log.d(TAG, "onItemclick: -----------"+keyword);
                 searchSwith(keyword);
             }
         });
        mContent.setOnRetryClickListener(new UILoader.onRetryClickListener() {
            @Override
            public void RetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mContent.updateStatus(UILoader.UIstatus.LOADING);
                }
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSeachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去执行搜索
                String keyword = mInput.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    Toast.makeText(SearchActivity.this, "搜索关键字不能为空。。。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    mContent.updateStatus(UILoader.UIstatus.LOADING);
                }
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mDeleteBtn.setVisibility(View.GONE);

                }else{
                    mDeleteBtn.setVisibility(View.VISIBLE);
                    if (isNeedSuggestion) {
                        //触发联想查询
                        getSuggestWord(s.toString());
                    }else{
                       isNeedSuggestion = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchSwith(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "搜索关键字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //第一步，把热词扔到输入框里
        mInput.setText(text);
        mInput.setSelection(text.length());
        // 第二步：发起搜索
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(text);
        }
        //改变状态
        if (mContent != null) {
            mContent.updateStatus(UILoader.UIstatus.LOADING);
        }
    }

    private void getSuggestWord(String keyWord) {
        LogUtil.d(TAG,"");
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyWord);
        }
    }


    private void initView() {
        mResultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mResultListView = mResultView.findViewById(R.id.result_list_view);
        mSearchRecommend = mResultView.findViewById(R.id.search_recommend_list);
        mAdapter = new SearchRecommendAdapter();
        mAlbumListAdapter = new AlbumListAdapter();
        //显示热词的
        mFlowTextLayout = mResultView.findViewById(R.id.recommend_hot_word_view);
        //刷新控件
        mRefreshLayout = mResultView.findViewById(R.id.refresh_result_list);

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mBackBtn = findViewById(R.id.search_back);
        mDeleteBtn = findViewById(R.id.search_input_delete);//删除热词按钮
        mDeleteBtn.setVisibility(View.GONE);
        mInput = findViewById(R.id.search_input);
        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInput.requestFocus();
                mInputMethodManager1.showSoftInput(mInput,InputMethodManager.SHOW_IMPLICIT);
            }
        },300);
        mSeachBtn = findViewById(R.id.search_txt_btn);
        mSearchContainer = findViewById(R.id.search_container);
        if (mContent==null) {
            mContent = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup containner) {
                    return createSuccessView();
                }
            };
            if (mContent.getParent() instanceof ViewGroup) {
                ((ViewGroup) mContent.getParent()).removeView(mContent);//不能重复添加
            }
        }
        mSearchContainer.addView(mContent);
    }

    /**
     * 创建数据请求成功的VIEW
     * @return
     * @param
     */
    private View createSuccessView() {
       /* View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //显示热词的
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);*/
        //设置布局管理器
        mResultListView.setLayoutManager(new LinearLayoutManager(this));
        //设置适配器

        mResultListView.setAdapter(mAlbumListAdapter);
        mRefreshLayout.setEnableRefresh(false);

        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);


            }
        });
        //设置布局管理器，
        mSearchRecommend.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecommend.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //设置适配器
        mSearchRecommend.setAdapter(mAdapter);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        return mResultView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
       /* if (mFlowTextLayout != null && mResultListView != null) {*/
        handleSearchResult(result);
        //隐藏键盘
        mInputMethodManager1.hideSoftInputFromWindow(mInput.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void handleSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size() ==0) {
                //数据为空
                if (mContent != null) {
                    mContent.updateStatus(UILoader.UIstatus.EMPTY);
                }
            }else{
                //如果数据bu1为空
                mAlbumListAdapter.setData(result);
                mContent.updateStatus(UILoader.UIstatus.SUCCESS);

            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        //做一个热词缓存
        /*if (mFlowTextLayout != null && mResultListView != null){*/
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mContent != null) {
            mContent.updateStatus(UILoader.UIstatus.SUCCESS);
        }
            LogUtil.d(TAG, "hotwordList -- ---->size" + hotWordList.size());

            List<String> hotwords = new ArrayList<>();
            hotwords.clear();
            for (HotWord hotWord : hotWordList) {
                String searchword = hotWord.getSearchword();
                hotwords.add(searchword);
            }
            Collections.sort(hotwords);
            //更新ui
        /*if (mFlowTextLayout != null) {*/
            mFlowTextLayout.setTextContents(hotwords);

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        //
        if(isOkay){
            handleSearchResult(result);
        }else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keywordList) {
        //关键字的联想
        if (mAdapter != null) {
            mAdapter.setData(keywordList);
        }
       //控制ui的状态和隐藏显示
        if (mContent != null) {
            mContent.updateStatus(UILoader.UIstatus.SUCCESS);
        }
        //控制显示隐藏
        hideSuccessView();
        mSearchRecommend.setVisibility(View.VISIBLE);

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        if (mContent != null) {
            mContent.updateStatus(UILoader.UIstatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView(){
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
        mSearchRecommend.setVisibility(View.GONE);

    }

}