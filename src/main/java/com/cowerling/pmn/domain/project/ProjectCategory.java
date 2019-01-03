package com.cowerling.pmn.domain.project;

public enum ProjectCategory {
    /*CP0("框架控制网CP0"), CPI("基础平面控制网CPI"), CPII("线路平面控制网CPII"), CPIII("轨道控制网CPIII"), CPII_LE("线上加密CPII"), TSIT("洞内导线"), EC("高程控制网");*/
    NORMAL_RS("常规铁路测绘项目"), OTHER_PMN("其他类型精测网项目");

    private String description;

    ProjectCategory(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
