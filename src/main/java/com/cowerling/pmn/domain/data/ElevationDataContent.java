package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;

import java.util.Date;

@Alias("elevationDataContent")
public class ElevationDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double adjustedValue;
    @FieldOrder(order = 2)
    private String grade;
    @FieldOrder(order = 3)
    private String period;
    @FieldOrder(order = 4)
    private Date finishDate;
    @FieldOrder(order = 5)
    private String team;
    @FieldOrder(order = 6)
    private Double update;
    @FieldOrder(order = 7)
    private String remark;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAdjustedValue() {
        return adjustedValue;
    }

    public void setAdjustedValue(Double adjustedValue) {
        this.adjustedValue = adjustedValue;
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

    public Double getUpdate() {
        return update;
    }

    public void setUpdate(Double update) {
        this.update = update;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Geometry getGeometry() {
        return null;
    }

    @Override
    public String getTableName() {
        return "t_data_content_elevation";
    }
}
