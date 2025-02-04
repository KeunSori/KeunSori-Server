package com.keunsori.keunsoriserver.global.util;

import java.time.DayOfWeek;

public class DayOfWeekUtil {
    public static int getCustomDayValue(DayOfWeek dayOfWeek){
        return dayOfWeek.getValue() % 7;
    }

    public static DayOfWeek fromCustomDayValue(int customValue){
        return DayOfWeek.of((customValue == 0) ? 7 : customValue);
    }
}
