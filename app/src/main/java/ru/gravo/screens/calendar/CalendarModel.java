package ru.gravo.screens.calendar;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import ru.gravo.App;
import ru.gravo.base.DataBox;
import ru.gravo.base.RequestModel;
import ru.gravo.boxes.BpoLc;
import ru.gravo.boxes.work_place_list.LcCfgWorkPlace;
import ru.gravo.boxes.worker_plan_list.WorkerPlanListBox;
import ru.gravo.boxes.worker_table_list.WorkerTableListBox;

/**
 *
 */

public class CalendarModel extends RequestModel {

    public Observable<Result<DataBox<BpoLc<LcCfgWorkPlace>>>> getWorkPlaceList(){
        return App.getServerApi().getWorkPlaceList();
    }

    public Observable<Result<DataBox<WorkerTableListBox>>> getWorkerTableList(String workerKey,
                                                                              String workGroupKey,
                                                                              int ym) {
        return App.getServerApi().getWorkerTableList(workerKey, workGroupKey, ym);
    }

    public Observable<Result<DataBox<WorkerPlanListBox>>> getWorkerPlanList(String workerKey,
                                                                             String workGroupKey,
                                                                             int ym) {
        return App.getServerApi().getWorkerPlanList(workerKey, workGroupKey, ym);
    }
}
