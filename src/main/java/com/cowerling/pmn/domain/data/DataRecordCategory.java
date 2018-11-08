package com.cowerling.pmn.domain.data;

public enum DataRecordCategory {
    CP0("CP0数据", "cp0-templet", true),
    CPI_2D("CPI二维数据", "cpi-2d-templet", true),
    CPI_3D("CPI三维维数据", "cpi-3d-templet", true),
    CPII("CPII数据", "cpii-templet", true),
    CPIII("CPIII数据", "cpiii-templet", true),
    CPII_LE("CPII线上加密数据", "cpii-le-templet", true),
    TSIT("洞内导线数据", "tsit-templet", true),
    EC("高程控制数据", "ec-templet", false);

    private String description;
    private String templet;
    private boolean spatial;

    DataRecordCategory(String description, String templet, boolean spatial) {
        this.description = description;
        this.templet = templet;
        this.spatial = spatial;
    }

    public String getTemplet() {
        return templet;
    }

    public boolean isSpatial() {
        return spatial;
    }

    @Override
    public String toString() {
        return description;
    }
}
