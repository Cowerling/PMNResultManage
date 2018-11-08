package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;
import org.postgis.Point;

@Alias("cpi3dDataContent")
public class CPI3DDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double x;
    @FieldOrder(order = 2)
    private Double y;
    @FieldOrder(order = 3)
    private Double z;
    @FieldOrder(order = 4)
    private Double mx;
    @FieldOrder(order = 5)
    private Double my;
    @FieldOrder(order = 6)
    private Double mz;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getMx() {
        return mx;
    }

    public void setMx(Double mx) {
        this.mx = mx;
    }

    public Double getMy() {
        return my;
    }

    public void setMy(Double my) {
        this.my = my;
    }

    public Double getMz() {
        return mz;
    }

    public void setMz(Double mz) {
        this.mz = mz;
    }

    @Override
    public Geometry getGeometry() {
        return new Point(x, y, z);
    }

    @Override
    public String getTableName() {
        return "t_data_content_cpi_three_dimension";
    }
}
