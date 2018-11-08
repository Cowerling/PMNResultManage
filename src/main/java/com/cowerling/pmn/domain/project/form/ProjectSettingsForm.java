package com.cowerling.pmn.domain.project.form;

import javax.validation.constraints.Size;

public class ProjectSettingsForm {
    private String manager;
    private String principal;
    @Size(max = 200)
    private String remark;
    private String status;
    private String member;
    private boolean principalAdopt;
    private boolean managerAdopt;
    private boolean creatorAdopt;
    @Size(max = 200)
    private String principalRemark;
    @Size(max = 200)
    private String managerRemark;
    @Size(max = 200)
    private String creatorRemark;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public boolean getPrincipalAdopt() {
        return principalAdopt;
    }

    public void setPrincipalAdopt(boolean principalAdopt) {
        this.principalAdopt = principalAdopt;
    }

    public boolean getManagerAdopt() {
        return managerAdopt;
    }

    public void setManagerAdopt(boolean managerAdopt) {
        this.managerAdopt = managerAdopt;
    }

    public boolean getCreatorAdopt() {
        return creatorAdopt;
    }

    public void setCreatorAdopt(boolean creatorAdopt) {
        this.creatorAdopt = creatorAdopt;
    }

    public String getPrincipalRemark() {
        return principalRemark;
    }

    public void setPrincipalRemark(String principalRemark) {
        this.principalRemark = principalRemark;
    }

    public String getManagerRemark() {
        return managerRemark;
    }

    public void setManagerRemark(String managerRemark) {
        this.managerRemark = managerRemark;
    }

    public String getCreatorRemark() {
        return creatorRemark;
    }

    public void setCreatorRemark(String creatorRemark) {
        this.creatorRemark = creatorRemark;
    }
}
