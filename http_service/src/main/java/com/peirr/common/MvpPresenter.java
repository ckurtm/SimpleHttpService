package com.peirr.common;


public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
