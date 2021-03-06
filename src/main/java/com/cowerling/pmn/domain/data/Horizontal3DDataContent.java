package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;
import org.postgis.Point;

import java.util.Date;

@Alias("horizontal3dDataContent")
public class Horizontal3DDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double x;
    @FieldOrder(order = 2)
    private Double y;
    @FieldOrder(order = 3)
    private Double z;
    @FieldOrder(order = 4)
    private String grade;
    @FieldOrder(order = 5)
    private String period;
    @FieldOrder(order = 6)
    private Date finishDate;
    @FieldOrder(order = 7)
    private String team;
    @FieldOrder(order = 8)
    private Double updateX;
    @FieldOrder(order = 9)
    private Double updateY;
    @FieldOrder(order = 10)
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

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Geometry getGeometry() {
        return new Point(x, y, z);
    }

    @Override
    public String getTableName() {
        return "t_data_content_horizontal_three_dimension";
    }
}
