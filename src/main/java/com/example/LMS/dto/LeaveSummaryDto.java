package com.example.LMS.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeaveSummaryDto {
    private String monthKey;
    private Map<String, Integer> leaveCounts = new LinkedHashMap<>();

    public LeaveSummaryDto() {}

    public LeaveSummaryDto(String monthKey) {
        this.monthKey = monthKey;
    }

    public String getMonthKey() {
        return monthKey;
    }
    public void setMonthKey(String monthKey) {
        this.monthKey = monthKey;
    }

    public Map<String, Integer> getLeaveCounts() {
        return leaveCounts;
    }
    public void setLeaveCounts(Map<String, Integer> leaveCounts) {
        this.leaveCounts = leaveCounts;
    }
}
