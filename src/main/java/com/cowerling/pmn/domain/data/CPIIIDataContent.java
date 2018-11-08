package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;
import org.postgis.Point;

@Alias("cpiiiDataContent")
public class CPIIIDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double x;
    @FieldOrder(order = 2)
    private Double y;
    @FieldOrder(order = 3)
    private Double zenithHeight;
    @FieldOrder(order = 4)
    private Double prismHeight;

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

    public Double getZenithHeight() {
        return zenithHeight;
    }

    public void setZenithHeight(Double zenithHeight) {
        this.zenithHeight = zenithHeight;
    }

    public Double getPrismHeight() {
        return prismHeight;
    }

    public void setPrismHeight(Double prismHeight) {
        this.prismHeight = prismHeight;
    }

    @Override
    public Geometry getGeometry() {
        return new Point(y, x);
    }

    @Override
    public String getTableName() {
        return "t_data_content_cpiii";
    }
}
