package ru.gravo.api;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Query;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.GET;
import retrofit2.http.POST;

import ru.gravo.base.DataBox;
import ru.gravo.base.ParameterBox;
import ru.gravo.base.ResponseBox;
import ru.gravo.boxes.Bpo;
import ru.gravo.boxes.BpoA;

import ru.gravo.boxes.BpoLc;
import ru.gravo.boxes.active_shift.ShiftBox;

import ru.gravo.boxes.bootup.BootUpBox;

import ru.gravo.boxes.check_in_out.CheckInOutBox;
import ru.gravo.boxes.login.LoginBox;

import ru.gravo.boxes.user_registration.AAps;
import ru.gravo.boxes.work_group.AWorkGroup;
import ru.gravo.boxes.work_place_list.LcCfgWorkPlace;
import ru.gravo.boxes.worker_plan_list.WorkerPlanListBox;
import ru.gravo.boxes.worker_table_list.WorkerTableListBox;

/**
 *
 */

//put server methods declarations here
public interface RetrofitService {

    //FIXME: Set header values with interceptors
    String REFERRER = "referer: gravo.ru/mobile";

    @Multipart
    @Headers(REFERRER + "/token-ping")
    @POST("api/token-ping")
    Observable<Result<ResponseBox>> postTokenPing(@Part("p") RequestBody body);

    @Headers(REFERRER + "/registration")
    @GET("user/registration")
    Observable<Result<DataBox<BpoA<AAps>>>> getUserRegistration(@Query("s_u_rc") String regcode);

    @Multipart
    @Headers(REFERRER + "/registration")
    @POST("user/registration")
    Observable<Result<ResponseBox>> postUserRegistration(@Part("p") RequestBody body);

    @Headers(REFERRER + "/login")
    @GET("login/wog")
    Observable<Result<ResponseBox>> getLoginWog();

    @Multipart
    @Headers(REFERRER + "/login")
    @POST("login")
    Observable<Result<ParameterBox<LoginBox>>> postLogin(@Part("p") RequestBody body);

    @Multipart
    @Headers(REFERRER + "/login")
    @POST("login/access")
    Observable<Result<ResponseBox>> postLoginAccess(@Part("p") RequestBody body);

    @Headers(REFERRER + "/bootup")
    @GET("data/bootup")
    Observable<Result<DataBox<BootUpBox>>> getDataBootUp();

    @Headers(REFERRER + "/active-shift")
    @GET("api/active-shift")
    Observable<Result<ParameterBox<ShiftBox>>> getActiveShift(@Query("u") String workerKey,
                                                              @Query("wgr_i") String workGroupKey);

    @Multipart
    @Headers(REFERRER + "/active-shift")
    @POST("api/active-shift")
    Observable<Result<ParameterBox<ShiftBox>>> postActiveShift(@Part("p") RequestBody body);

    @Headers(REFERRER + "/worker-group")
    @GET("api/worker-group")
    Observable<Result<DataBox<BpoA<AWorkGroup>>>> getWorkerGroup(@Query("u") String key);

    @Headers(REFERRER + "/work-place/list")
    @GET("api/work-place/list")
    Observable<Result<DataBox<BpoLc<LcCfgWorkPlace>>>> getWorkPlaceList();

    @Multipart
    @Headers(REFERRER + "/op/check-in")
    @POST("api/op/check-in")
    Observable<Result<ParameterBox<CheckInOutBox>>> postCheckIn(@Part("p") RequestBody body);

    @Multipart
    @Headers(REFERRER + "/op/check-out")
    @POST("api/op/check-out")
    Observable<Result<ParameterBox<CheckInOutBox>>> postCheckOut(@Part("p") RequestBody body);

    @Headers(REFERRER + "/worker/table/list")
    @GET("api/worker/table/list")
    Observable<Result<DataBox<WorkerTableListBox>>> getWorkerTableList(@Query("u") String workerKey,
                                                                       @Query("wgr_i") String workGroupKey,
                                                                       @Query("ym") int ym);

    @Headers(REFERRER + "/worker/plan/list")
    @GET("api/worker/plan/list")
    Observable<Result<DataBox<WorkerPlanListBox>>> getWorkerPlanList(@Query("u") String workerKey,
                                                                     @Query("wgr_i") String workGroupKey,
                                                                     @Query("ym") int ym);
}
