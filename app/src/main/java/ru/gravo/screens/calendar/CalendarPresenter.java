package ru.gravo.screens.calendar;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.Result;
import ru.gravo.DBHelper;
import ru.gravo.api.DataTransform;
import ru.gravo.base.DataBox;
import ru.gravo.base.observers.CapsuleZip;
import ru.gravo.base.observers.ObserverLink;
import ru.gravo.base.observers.ViewCapsuleZipObserver;
import ru.gravo.base.observers.ViewDataBoxObserver;
import ru.gravo.boxes.BpoLc;
import ru.gravo.boxes.work_place_list.LcCfgWorkPlace;
import ru.gravo.boxes.work_place_list.WorkPlaceListYota;
import ru.gravo.boxes.worker_plan_list.WorkerPlanListBox;
import ru.gravo.boxes.worker_table_list.WorkerTableListBox;
import ru.gravo.screens.history.models.HistoryMonthModel;
import ru.gravo.utils.CalendarHelper;

/**
 *
 */

public class CalendarPresenter extends MvpBasePresenter<CalendarView> {

    private CalendarModel model;
    private HashMap<String, WorkPlaceListYota> workPlaceMap;
    private long timeCursor = -1;

    //boolean table = false, plan = false;
    private Disposable disposable;

    public CalendarPresenter() {
        model = new CalendarModel();
    }

    //FIXME: move to common functional - cache class? reduce duplicates
    private void loadWorkPlaces(final ObserverLink link) {
        loadWorkPlaces(link, false);
    }

    private void loadWorkPlaces(final ObserverLink link, boolean ignoreCache) {
        if (ignoreCache || DBHelper.needCacheUpdate(DBHelper.WORK_PLACES_ID)) {
            disposable = model.getWorkPlaceList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new ViewDataBoxObserver<BpoLc<LcCfgWorkPlace>>(getView()) {
                        @Override
                        public void processData(BpoLc<LcCfgWorkPlace> data) {
                            workPlaceMap = DataTransform.genWorkPlaceMap(data);
                            DBHelper.saveWorkPlaceMap(workPlaceMap);
                            DBHelper.saveCacheLastUpdate(DBHelper.WORK_PLACES_ID);

                            link.process();
                        }
                    });
        }
        else {
            if (workPlaceMap == null) {
                workPlaceMap = DBHelper.getWorkPlaceMap();
            }
            //else{ nop }

            if(workPlaceMap == null){
                loadWorkPlaces(link, true);
            }
            else {
                link.process();
            }
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        //else{ nop }
    }

    void getWorkerSchedule(final List<HistoryMonthModel> monthList, final int direction) {
        getView().showLoading();
        getView().clearWorkPlaces();

        final long nowTime = CalendarHelper.getNowWithOffset();
        if (direction == 0) {
            timeCursor = (timeCursor < 0 ? nowTime : timeCursor);
        }
        else if (direction < 0) {
            timeCursor = CalendarHelper.getPreviousMonthTime(timeCursor);
        }
        else {
            timeCursor = CalendarHelper.getNextMonthTime(timeCursor);
        }

        final int compareWithNow = CalendarHelper.compareMonths(nowTime, timeCursor);
        if (compareWithNow == 0) {
            timeCursor = nowTime;
            getView().highlightWeekDayName(CalendarHelper.getEcmaDayOfWeekUTC(nowTime));
        }
        else {
            getView().highlightWeekDayName(-1);
        }

        getView().setMonthTitle(CalendarHelper.getMonthYearStringUTC(timeCursor));


        loadWorkPlaces(new ObserverLink() {
            @Override
            public void process() {

                final long prevMonthTime = CalendarHelper.getPreviousMonthTime(timeCursor);
                final long nextMonthTime = CalendarHelper.getNextMonthTime(timeCursor);

                final ObserverLink clearMonthsLink = new ObserverLink() {
                    @Override
                    public void process() {
                        getView().hideLoading();
                        monthList.get(0).clear(prevMonthTime);
                        monthList.get(1).clear(timeCursor);
                        monthList.get(2).clear(nextMonthTime);

                        monthList.get(1).setMonthAsCurrent(compareWithNow == 0);
                    }
                };

                final ObserverLink updateSelectedMonthViewLink = new ObserverLink() {
                    @Override
                    public void process() {
                        getView().updateMonthView();
                        getView().setWorkPlaces(workPlaceMap, monthList.get(1).getWorkPlaceKeySet());
                    }
                };

                final ObserverLink communicateErrorLink = new ObserverLink() {
                    @Override
                    public void process() {
                        if (monthList != null && monthList.size() > 0) {
                            monthList.get(0).clear(prevMonthTime);
                            monthList.get(1).clear(timeCursor);
                            monthList.get(2).clear(nextMonthTime);
                            getView().updateMonthView();
                        }
                        //else{ nop }
                    }
                };

                if (compareWithNow == 0) {
                    //get tables and plans
                    disposable = Observable.zip(
                            model.getWorkerTableList(
                                    DBHelper.getWorkerKey(),
                                    DBHelper.getWorkGroupKey(),
                                    CalendarHelper.getYmUTC(timeCursor))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()),
                            model.getWorkerPlanList(
                                    DBHelper.getWorkerKey(),
                                    DBHelper.getWorkGroupKey(),
                                    CalendarHelper.getYmUTC(timeCursor))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()),
                            new BiFunction<Result<DataBox<WorkerTableListBox>>, Result<DataBox<WorkerPlanListBox>>, CapsuleZip<DataBox<WorkerTableListBox>, DataBox<WorkerPlanListBox>>>() {
                                @Override
                                public CapsuleZip<DataBox<WorkerTableListBox>, DataBox<WorkerPlanListBox>>
                                apply(Result<DataBox<WorkerTableListBox>> one, Result<DataBox<WorkerPlanListBox>> two) throws Exception {
                                    return new CapsuleZip<>(one, two);
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new ViewCapsuleZipObserver<DataBox<WorkerTableListBox>, DataBox<WorkerPlanListBox>>(getView()) {
                                @Override
                                public void process(DataBox<WorkerTableListBox> dataOne, DataBox<WorkerPlanListBox> dataTwo) {
                                    //FIXME: create combination classes? params + params, params + data, data + data
                                    if (isViewAttached()) {
                                        getView().hideLoading();
                                        if (monthList != null && monthList.size() > 0) {
                                            clearMonthsLink.process();
                                            monthList.get(1).fill(workPlaceMap, dataOne.getData(), dataTwo.getData());
                                            updateSelectedMonthViewLink.process();
                                        }
                                        //else{ nop }
                                    }
                                    //else{ nop }
                                }

                                @Override
                                protected void onCommunicateError(IOException e) {
                                    if (isViewAttached()) {
                                        super.onCommunicateError(e);
                                        communicateErrorLink.process();
                                    }
                                    //else{ nop }
                                }
                            });
                }
                else if (compareWithNow < 0) {

                    disposable = model.getWorkerPlanList(
                            DBHelper.getWorkerKey(),
                            DBHelper.getWorkGroupKey(),
                            CalendarHelper.getYmUTC(timeCursor))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new ViewDataBoxObserver<WorkerPlanListBox>(getView()) {
                                @Override
                                public void processData(WorkerPlanListBox plans) {
                                    if (isViewAttached()) {
                                        if (monthList != null && monthList.size() > 0) {
                                            clearMonthsLink.process();
                                            monthList.get(1).fill(workPlaceMap, plans);
                                            updateSelectedMonthViewLink.process();
                                        }
                                        //else{ nop }
                                    }
                                    //else{ nop }
                                }

                                @Override
                                protected void onCommunicateError(IOException e) {
                                    if (isViewAttached()) {
                                        super.onCommunicateError(e);
                                        communicateErrorLink.process();
                                    }
                                    //else{ nop }
                                }
                            });
                }
                else {
                    //get tables only
                    disposable = model.getWorkerTableList(
                            DBHelper.getWorkerKey(),
                            DBHelper.getWorkGroupKey(),
                            CalendarHelper.getYmUTC(timeCursor))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new ViewDataBoxObserver<WorkerTableListBox>(getView()) {
                                @Override
                                public void processData(WorkerTableListBox tables) {
                                    if (isViewAttached()) {
                                        if (monthList != null && monthList.size() > 0) {
                                            clearMonthsLink.process();
                                            monthList.get(1).fill(workPlaceMap, tables);
                                            updateSelectedMonthViewLink.process();
                                        }
                                        //else{ nop }
                                    }
                                    //else{ nop }
                                }

                                @Override
                                protected void onCommunicateError(IOException e) {
                                    if (isViewAttached()) {
                                        super.onCommunicateError(e);
                                        communicateErrorLink.process();
                                    }
                                    //else{ nop }
                                }
                            });
                }
            }
        });
    }
}
