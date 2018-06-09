package com.cowerling.pmn.domain.project;

public enum ProjectStatus {
    WAIT("未开始"), PROGRESS("进行中"), FINISH("结束");

    private String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String format() {
        return "project-" + name().toLowerCase().replace('_', '-');
    }
}
