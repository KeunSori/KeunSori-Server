package com.keunsori.keunsoriserver.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    /**
     * 202401 -> 2024-01-01
     * */
    public static LocalDate parseMonthToFirstDate(String yyyyMM) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(yyyyMM + "01", formatter);
    }
}
