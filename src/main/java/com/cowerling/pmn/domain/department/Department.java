package com.cowerling.pmn.domain.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ibatis.type.Alias;

@Alias("department")
public class Department {
    @JsonIgnore
    private Long id;
    private String name;
    private int specificNumber;
    private String tag;

    public Department() {
        specificNumber = -1;
    }

    public Department(String name) {
        this();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpecificNumber() {
        return specificNumber;
    }

    public void setSpecificNumber(int specificNumber) {
        this.specificNumber = specificNumber;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
