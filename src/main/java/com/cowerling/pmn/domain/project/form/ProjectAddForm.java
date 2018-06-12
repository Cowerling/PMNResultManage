package com.cowerling.pmn.domain.project.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectAddForm {
    @NotNull
    @Size(max = 100)
    private String name;
    @NotNull
    private String category;
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
