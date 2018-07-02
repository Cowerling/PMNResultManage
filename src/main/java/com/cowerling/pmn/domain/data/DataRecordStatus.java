package com.cowerling.pmn.domain.data;

public enum DataRecordStatus {
    UNAUDITED("未审核"), QUALIFIED("合格"), UNQUALIFIED("不合格");

    private String description;

    DataRecordStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String format() {
        return "data-record-" + name().toLowerCase().replace('_', '-');
    }
}
