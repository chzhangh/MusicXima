package com.example.musicxima.base;



public interface IBasePresenter<T> {
    /**
     * 用于注册ui的回调
     * @param
     */
    void registerViewCallback(T m);

    /**
     * 反注册UI的回调
     * @param
     */
    void unRegisterViewCallback(T m);
}
