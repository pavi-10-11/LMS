package com.example.LMS.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeaveSummaryDto {
    private String monthKey;
    private Map<String, Integer> leaveCounts = new LinkedHashMap<>();
    private int totalCount;
    public LeaveSummaryDto() {}


    public LeaveSummaryDto(String monthKey) {
        this.monthKey = monthKey;
    }


    public LeaveSummaryDto(String monthKey, int totalCount) {
        this.monthKey = monthKey;
        this.totalCount = totalCount;
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

    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
