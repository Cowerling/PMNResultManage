package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;
import org.postgis.Point;

import java.util.Date;

@Alias("cpiiielevationDataContent")
public class CPIIIElevationDataContent extends DataContent {
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
    @FieldOrder(order = 4)
    private String period;
    @FieldOrder(order = 5)
    private Date finishDate;
    @FieldOrder(order = 6)
    private String team;
    @FieldOrder(order = 7)
    private Double updateX;
    @FieldOrder(order = 8)
    private Double updateY;
    @FieldOrder(order = 8)
    private Double updateH;
    @FieldOrder(order = 9)
    private String remark;

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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Double getUpdateX() {
        return updateX;
    }

    public void setUpdateX(Double updateX) {
        this.updateX = updateX;
    }

    public Double getUpdateY() {
        return updateY;
    }

    public void setUpdateY(Double updateY) {
        this.updateY = updateY;
    }

    public Double getUpdateH() {
        return updateH;
    }

    public void setUpdateH(Double updateH) {
        this.updateH = updateH;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Geometry getGeometry() {
        return new Point(y, x);
    }

    @Override
    public String getTableName() {
        return "t_data_content_cpiii_elevation";
    }
}
