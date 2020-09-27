package ru.gravo.base.views;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Алексей on 22.11.2017.
 */

public interface NotifiableView {
    String UPDATE_VIEW_ACTION = "ru.gravo.base.UPDATE_VIEW";
    void update(Context context, Intent intent);
}
