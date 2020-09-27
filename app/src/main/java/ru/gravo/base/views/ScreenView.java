package ru.gravo.base.views;

import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 *
 */

public interface ScreenView extends MvpView {
    void showError(String msg);
    void hideError();
    void showLoading();
    void hideLoading();
    void redirect(String url);
}
