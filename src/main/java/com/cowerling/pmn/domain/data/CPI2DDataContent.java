package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;
import org.postgis.Point;

@Alias("cpi2dDataContent")
public class CPI2DDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double x;
    @FieldOrder(order = 2)
    private Double y;
    @FieldOrder(order = 3)
    private Double mx;
    @FieldOrder(order = 4)
    private Double my;
    @FieldOrder(order = 5)
    private Double mp;

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

    public Double getMp() {
        return mp;
    }

    public void setMp(Double mp) {
        this.mp = mp;
    }

    @Override
    public Geometry getGeometry() {
        return new Point(y, x);
    }

    @Override
    public String getTableName() {
        return "t_data_content_cpi_two_dimension";
    }
}
