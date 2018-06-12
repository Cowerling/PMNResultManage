package com.cowerling.pmn.domain.project.form;

import javax.validation.constraints.Size;

public class ProjectSettingsForm {
    private String manager;
    private String principal;
    @Size(max = 200)
    private String remark;

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
