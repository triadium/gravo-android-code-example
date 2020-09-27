package ru.gravo.base.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import ru.gravo.base.views.ScreenView;

/**
 * Created by Алексей on 22.11.2017.
 */

public abstract class MvpBaseScreenController<V extends ScreenView, P extends MvpBasePresenter<V>>
        extends MvpBaseButterKnifeController<V, P> implements ScreenView {
    private static String TAG = "SCREEN-CTRL";

    //Screen does not handle "back" itself by default
    private boolean handleBack = false;

    public MvpBaseScreenController() {}

    public MvpBaseScreenController(@Nullable Bundle args) {super(args);}

    public void setHandleBack(boolean handle) {
        handleBack = handle;
    }

    @Override
    public boolean handleBack() {
        return (!handleBack || super.handleBack());
    }

    protected abstract String getLoginUrl();
    protected abstract void pushAuthorization();

    public void redirect(String url) {
        //Log.d(TAG, "redirect to " + url);

        hideLoading();
        if (url.equals(getLoginUrl())) {
            pushAuthorization();
        }
        //else{ nop }
    }

    //for convenience
    protected void setLoadingVisible(Boolean f){}

    public void showLoading() {
        setLoadingVisible(true);
    }

    public void hideLoading() {
        setLoadingVisible(false);
    }
}
