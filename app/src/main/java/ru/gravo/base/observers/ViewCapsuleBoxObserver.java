package ru.gravo.base.observers;

import ru.gravo.base.CapsuleBox;
import ru.gravo.base.views.ScreenView;

/**
 * Created by Алексей on 02.11.2017.
 */

public class ViewCapsuleBoxObserver<D, P> extends ViewCapsuleObserver<CapsuleBox<D, P>> {

    public ViewCapsuleBoxObserver(ScreenView view){
        super(view);
    }

    @Override
    final public void process(CapsuleBox<D, P> box) {
        D data = box.getData();
        P params = box.getParameters();

        process(data, params);
    }

    public void process(D data, P params) {
    }
}