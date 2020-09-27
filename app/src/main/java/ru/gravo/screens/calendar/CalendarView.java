package ru.gravo.screens.calendar;

import java.util.HashMap;
import java.util.HashSet;

import ru.gravo.base.views.ScreenView;
import ru.gravo.boxes.work_place_list.WorkPlaceListYota;


/**
 *
 */

public interface CalendarView extends ScreenView {
    void updateMonthView();
    void setMonthTitle(String title);
    void highlightWeekDayName(int index);
    void clearWorkPlaces();
    void setWorkPlaces(HashMap<String, WorkPlaceListYota> workPlaceMap, HashSet<String> keys);
}
