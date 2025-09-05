package com.keunsori.keunsoriserver.global.util;

import java.time.DayOfWeek;
import java.util.Map;

public class DayOfWeekUtil {
    private static final Map<String, DayOfWeek> SHORT_DAY_OF_WEEK_MAP = Map.of(
            "SUN", DayOfWeek.SUNDAY,
            "MON", DayOfWeek.MONDAY,
            "TUE", DayOfWeek.TUESDAY,
            "WED", DayOfWeek.WEDNESDAY,
            "THU", DayOfWeek.THURSDAY,
            "FRI", DayOfWeek.FRIDAY,
            "SAT", DayOfWeek.SATURDAY
    );

    public static int getCustomDayValue(DayOfWeek dayOfWeek){
        return dayOfWeek.getValue() % 7;
    }

    public static DayOfWeek fromCustomDayValue(int customValue){
        return DayOfWeek.of((customValue == 0) ? 7 : customValue);
    }

    public static DayOfWeek fromString(String value) {
        String upper = value.toUpperCase();

        if(SHORT_DAY_OF_WEEK_MAP.containsKey(upper)){
            return SHORT_DAY_OF_WEEK_MAP.get(upper);
        }
        
        return DayOfWeek.valueOf(upper);
    }
}
