package com.cowerling.pmn.domain.project;

public enum ProjectCategory {
    CPI("基础平面控制网"), CPII("线路控制网"), CPIII("基桩控制网"), LLBP("线路水准基点控制网"), OELM("线上加密水准的测量");

    private String description;

    ProjectCategory(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
