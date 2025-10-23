package com.example.LMS.dto;

import java.util.HashMap;
import java.util.Map;

public class LeaveSummaryDto {

    private String month;
    private Map<String, Integer> leaveCounts = new HashMap<>();

    public LeaveSummaryDto() {}

    public LeaveSummaryDto(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Map<String, Integer> getLeaveCounts() {
        return leaveCounts;
    }

    public void setLeaveCounts(Map<String, Integer> leaveCounts) {
        if (leaveCounts == null) {
            this.leaveCounts = new HashMap<>();
        } else {
            this.leaveCounts = leaveCounts;
        }
    }
}
