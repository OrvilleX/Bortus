package com.orvillex.bortus.manager.modules.scheduler.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogReportDto {
    private Long triggerDayCount;
    private Long triggerDayCountRunning;
    private Long triggerDayCountSuc;
}
