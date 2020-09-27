package ru.gravo.controllers;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluelinelabs.conductor.RouterTransaction;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import ru.gravo.App;
import ru.gravo.DBHelper;
import ru.gravo.R;
import ru.gravo.base.controllers.MvpBaseScreenController;
import ru.gravo.base.views.ScreenView;
import ru.gravo.screens.authorization.AuthorizationController;

/**
 * Created by Алексей on 28.10.2017.
 */

public abstract class MvpScreenController<V extends ScreenView, P extends MvpBasePresenter<V>> extends MvpBaseScreenController<V, P> {

    private String loginUrl;

    @Override
    protected String getLoginUrl() {
        if(loginUrl == null) {
            Resources resources = getResources();
            if(resources != null) {
                loginUrl = resources.getString(R.string.service_protocol) + "://" +
                        resources.getString(R.string.service_base_url) +
                        resources.getString(R.string.service_login_path);
            }
            //else{ nop }
        }
        //else{ nop }
        return loginUrl;
    }

    public MvpScreenController() {}

    public MvpScreenController(@Nullable Bundle args) {super(args);}

    @Override
    protected void pushAuthorization() {
        DBHelper.clearAll();
        App.getWorkTimeScheduler().cancelAllJobs();
        getParentController().getRouter().setRoot(RouterTransaction.with(new AuthorizationController()));
    }
}
