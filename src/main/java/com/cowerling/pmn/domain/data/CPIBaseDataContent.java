package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.CoordinateH;
import com.cowerling.pmn.annotation.CoordinateX;
import com.cowerling.pmn.annotation.CoordinateY;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

@Alias("cpiBaseDataContent")
public class CPIBaseDataContent extends DataContent {
    @CoordinateX
    private Double x;
    @CoordinateY
    private Double y;
    @CoordinateH
    private Double h;

    public CPIBaseDataContent() {
        this.crs = DataContent.EPSG_WGS84;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getH() {
        return h;
    }

    public void setH(Double h) {
        this.h = h;
    }
}
