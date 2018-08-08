package com.cowerling.pmn.domain.data;

public enum DataRecordCategory {
    CPI_BASE("CPI基础数据");

    private String description;

    DataRecordCategory(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
