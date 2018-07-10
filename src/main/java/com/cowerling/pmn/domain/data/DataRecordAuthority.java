package com.cowerling.pmn.domain.data;

public enum  DataRecordAuthority {
    BASIS("概况"), VIEW("查阅"), DOWNLOAD("下载"), EDIT("修改"), DELETE("删除");

    private String description;

    DataRecordAuthority(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String format() {
        return "data-record-authority-" + name().toLowerCase().replace('_', '-');
    }
}
