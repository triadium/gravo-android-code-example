package ru.gravo.screens.calendar;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import ru.gravo.App;
import ru.gravo.DBHelper;
import ru.gravo.ExtraLog;
import ru.gravo.R;
import ru.gravo.base.NotifiableData;
import ru.gravo.boxes.work_place_list.WorkPlaceListYota;
import ru.gravo.controllers.MvpScreenController;
import ru.gravo.screens.history.models.HistoryMonthModel;
import ru.gravo.utils.CalendarHelper;
import ru.gravo.views.ManagingViewPager;

/**
 *
 */

public class CalendarController extends MvpScreenController<CalendarView, CalendarPresenter> implements CalendarView {

    @BindView(R.id.mvpSelectedMonth)
    ManagingViewPager mvpSelectedMonth;
    @BindView(R.id.tvSelectedMonth)
    TextView tvSelectedMonth;
    @BindView(R.id.btnNext)
    ImageButton btnNext;
    @BindView(R.id.btnPrevious)
    ImageButton btnPrevious;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.flTags)
    FlexboxLayout flTags;

    @BindView(R.id.tvMonday)
    TextView tvMonday;
    @BindView(R.id.tvTuesday)
    TextView tvTuesday;
    @BindView(R.id.tvWednesday)
    TextView tvWednesday;
    @BindView(R.id.tvThursday)
    TextView tvThursday;
    @BindView(R.id.tvFriday)
    TextView tvFriday;
    @BindView(R.id.tvSaturday)
    TextView tvSaturday;
    @BindView(R.id.tvSunday)
    TextView tvSunday;

    private long currentDayTime = -1;
    private TextView[] weekDayViewList;

    private List<HistoryMonthModel> monthList;
    private RouterPagerAdapter monthPagerAdapter;
    private ViewPager.OnPageChangeListener monthChangeListener;

    private static final String TAG = "CLNDR-CTRL";

    public CalendarController() {
        setup();
    }

    public CalendarController(@Nullable Bundle args) {
        super(args);
        setup();
    }

    private void setup(){
        monthList = new ArrayList<HistoryMonthModel>(3);
        for (int i = 0; i < 3; i++) {
            monthList.add(new HistoryMonthModel());
        }

        monthPagerAdapter = createMonthPagerAdapter();
        monthPagerAdapter.setMaxPagesToStateSave(0);

        monthChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            private int direction = 0;
            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    setCurrentDayTitle();

                    for (int i = 0; i < 3; i++) {
                        Router router =  monthPagerAdapter.getRouter(i);
                        if(router != null){
                            ((NotifiableData<HistoryMonthModel>)router.getBackstack().get(0).controller()).update(monthList.get(i));
                        }
                        //else{ nop }
                    }
                }
                //else{ nop }

                direction = position - 1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    getPresenter().getWorkerSchedule(monthList, direction);
                }
                //else{ nop }
            }
        };
    }

    private RouterPagerAdapter createMonthPagerAdapter(){
        return new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    //Log.d(TAG, "configureRouter: " + position);
                    Controller page = new CalendarMonthController(monthList.get(position));
                    router.setRoot(RouterTransaction.with(page));
                }
                //else{ nop }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mvpSelectedMonth.addOnPageChangeListener(monthChangeListener);
        getPresenter().getWorkerSchedule(monthList, 0);
    }

    @Override
    protected void onDetach(@NonNull View view) {
        for (Router router1 : getChildRouters()) {
            router1.getBackstack().clear();
        }
        mvpSelectedMonth.removeOnPageChangeListener(monthChangeListener);

        super.onDetach(view);
    }


    @Override
    public View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.screen_calendar, container, false);
    }

    private void setCurrentDayTitle(){
        long timeNow = CalendarHelper.getNowWithOffset();
        if(currentDayTime == -1 || !CalendarHelper.datesEqual(currentDayTime, timeNow)) {
            currentDayTime = timeNow;
            tvDay.setText(Integer.toString(CalendarHelper.getDayUTC(timeNow)));
            tvMonth.setText(CalendarHelper.getGenitiveCaseMonth(timeNow));
        }
        //else{ nop }
    }

    @Override
    public void onViewBound(@NonNull View view) {
        ExtraLog.initialize();
        DBHelper.initialize();
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        setHandleBack(false);

        setCurrentDayTitle();

        weekDayViewList = new TextView[]{tvSunday, tvMonday, tvTuesday, tvWednesday, tvThursday, tvFriday, tvSaturday};
        mvpSelectedMonth.setOffscreenPageLimit(3);
        mvpSelectedMonth.setAdapter(monthPagerAdapter);

        btnPrevious.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        btnNext.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpSelectedMonth.setCurrentItem(0, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpSelectedMonth.setCurrentItem(2, true);
            }
        });
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        if (!getActivity().isChangingConfigurations()) {
            mvpSelectedMonth.setAdapter(null);
        }
        //else{ nop }

        super.onDestroyView(view);
    }

    @NonNull
    @Override
    public CalendarPresenter createPresenter() {
        return new CalendarPresenter();
    }

    @Override
    protected void setLoadingVisible(Boolean f) {
        setNavControlsEnabled(!f);
        flTags.setVisibility(!f ? View.VISIBLE : View.GONE);
        pbLoading.setVisibility(f ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String msg) {
        hideLoading();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideError() {
    }

    private void setNavControlsEnabled(boolean f) {
        mvpSelectedMonth.setPagingEnabled(f);
        btnPrevious.setEnabled(f);
        btnNext.setEnabled(f);
    }

    @Override
    public void updateMonthView() {
        mvpSelectedMonth.setVisibility(View.GONE);
        if(mvpSelectedMonth.getCurrentItem() == 1){
            monthChangeListener.onPageSelected(1);
        }
        else {
            mvpSelectedMonth.setCurrentItem(1, false);
        }
        mvpSelectedMonth.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMonthTitle(String title) {
        tvSelectedMonth.setText(title);
    }

    @Override
    public void clearWorkPlaces(){
        flTags.removeAllViews();
    }

    @Override
    public void setWorkPlaces(HashMap<String, WorkPlaceListYota> workPlaceMap, HashSet<String> keys) {
        for (String key: keys) {
            int padding = getResources().getDimensionPixelSize(R.dimen.calendar_work_place_padding);
            Drawable draw = App.getRDrawable(R.drawable.circle);
            TextView textView = new TextView(App.getContext());
            textView.setTextColor(App.getRColor(R.color.colorCalendarWorkPlaceAliasText));
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(padding << 1, 0, 0, 0);
            WorkPlaceListYota workPlace = workPlaceMap.get(key);
            if(workPlace == null){
                textView.setText("?");
                draw.setColorFilter(App.getRColor(R.color.darkGrey), PorterDuff.Mode.SRC_ATOP);
            }
            else{
                textView.setText(workPlace.getA().getAlias());
                draw.setColorFilter(workPlace.getA().getParsedColor(), PorterDuff.Mode.SRC_ATOP);
            }
            textView.setLayoutParams(layoutParams);
            textView.setCompoundDrawablesWithIntrinsicBounds(draw, null, null, null);
            textView.setCompoundDrawablePadding(padding);
            flTags.addView(textView);
        }
    }

    @Override
    public void highlightWeekDayName(int index){
        for (int i = 0; i < weekDayViewList.length; i++) {
            weekDayViewList[i].setTextColor(App.getRColor(R.color.colorDayOfWeekName));
        }

        if(index >= 0){
            weekDayViewList[index].setTextColor(App.getRColor(R.color.colorCurrentDayOfWeekName));
        }
        //else{ nop }
    }
}
