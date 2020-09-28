package com.example.musicxima.utils;

import com.example.musicxima.base.BaseFragment;
import com.example.musicxima.fragments.HistoryFragment;
import com.example.musicxima.fragments.RecommendFragment;
import com.example.musicxima.fragments.SubscibeFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    public final static int INDEX_RECOMEND = 0;
    public final static int INDEX_SUBSCIBE = 1;
    public final static int INDEX_HISTORY = 2;
    public final static int PAGE_COUNT = 3;

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index){
        BaseFragment fragment = sCache.get(index);
        if(fragment != null){
            return fragment;
        }
        switch (index) {
            case INDEX_RECOMEND:
                fragment = new RecommendFragment();
                break;
            case INDEX_SUBSCIBE:
                fragment = new SubscibeFragment();
                break;
            case INDEX_HISTORY:
                fragment = new HistoryFragment();
                break;
        }
        sCache.put(index,fragment);
        return fragment;
    }
}
