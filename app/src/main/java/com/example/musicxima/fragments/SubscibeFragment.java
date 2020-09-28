package com.example.musicxima.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicxima.R;
import com.example.musicxima.base.BaseFragment;

public class SubscibeFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_subscibe,container, false);
        return v;
    }
}