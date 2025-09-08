package com.keunsori.keunsoriserver.admin.init;

import com.keunsori.keunsoriserver.common.ApiTest;
import com.keunsori.keunsoriserver.global.init.WeeklyScheduleInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiTestWithWeeklyScheduleInit extends ApiTest {

    @Autowired
    private WeeklyScheduleInitializer weeklyScheduleInitializer;

    @BeforeEach
    void setupWeeklySchedule() {
        weeklyScheduleInitializer.initializeSchedules();
    }
}
