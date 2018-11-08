package com.cowerling.pmn.domain.project;

import org.apache.ibatis.type.Alias;

@Alias("projectVerification")
public class ProjectVerification {
    private boolean principalAdopt;
    private boolean managerAdopt;
    private boolean creatorAdopt;
    private String principalRemark;
    private String managerRemark;
    private String creatorRemark;

    public ProjectVerification() {
        principalAdopt = managerAdopt = creatorAdopt = false;
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
