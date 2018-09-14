package com.cowerling.pmn.domain.data;

public enum DataRecordCategory {
    CPI_BASE("CPI基础数据", "cpi-base-templet");

    private String description;
    private String templet;

    DataRecordCategory(String description, String templet) {
        this.description = description;
        this.templet = templet;
    }

    public String getTemplet() {
        return templet;
    }

    @Override
    public String toString() {
        return description;
    }
}
