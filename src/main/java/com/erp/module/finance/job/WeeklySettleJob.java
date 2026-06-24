package com.erp.module.finance.job;

import com.erp.module.finance.service.CostCalcService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Component
@RequiredArgsConstructor
public class WeeklySettleJob {
    private final CostCalcService costCalcService;

    // 每周一凌晨2点执行
    @Scheduled(cron = "0 0 2 ? * MON")
    public void weeklySettle() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastSunday = lastMonday.plusDays(6);
        int weekNum = lastMonday.get(WeekFields.ISO.weekOfWeekBasedYear());
        String period = lastMonday.getYear() + "-W" + String.format("%02d", weekNum);
        costCalcService.weeklyCalc(lastMonday, lastSunday, period);
        // 同步刷新备货建议
    }
}