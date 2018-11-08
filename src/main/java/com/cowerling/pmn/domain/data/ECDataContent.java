package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import org.apache.ibatis.type.Alias;
import org.postgis.Geometry;

@Alias("ecDataContent")
public class ECDataContent extends DataContent {
    @FieldOrder(order = 0)
    private String name;
    @FieldOrder(order = 1)
    private Double meanDeviation;
    @FieldOrder(order = 2)
    private Double squareError;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMeanDeviation() {
        return meanDeviation;
    }

    public void setMeanDeviation(Double meanDeviation) {
        this.meanDeviation = meanDeviation;
    }

    public Double getSquareError() {
        return squareError;
    }

    public void setSquareError(Double squareError) {
        this.squareError = squareError;
    }

    @Override
    public Geometry getGeometry() {
        return null;
    }

    @Override
    public String getTableName() {
        return "t_data_content_ec";
    }
}
