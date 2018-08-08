package com.cowerling.pmn.domain.data;

import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

@Alias("cpiBaseDataContent")
public class CPIBaseDataContent extends DataContent {
    private Long id;
    private Double x;
    private Double y;
    private Double h;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public List<String> attributeNames() {
        return new ArrayList<>() {
            {
                add("x");
                add("y");
                add("h");
            }
        };
    }

    @Override
    public List<Object> values() {
        return new ArrayList<>() {
            {
                add(x);
                add(y);
                add(h);
            }
        };
    }
}
