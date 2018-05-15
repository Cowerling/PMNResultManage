package com.cowerling.pmn.domain.project;

import com.cowerling.pmn.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Alias("project")
public class Project {
    @JsonIgnore
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private ProjectCategory category;
    @JsonIgnore
    private User creator;
    private Date createTime;
    @JsonIgnore
    private User manager;
    @JsonIgnore
    private User principal;
    @JsonIgnore
    private List<User> members = new ArrayList<User>();
    private String remark;
    private ProjectStatus status;

    public Project() {
        createTime = new Date();
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

    public ProjectCategory getCategory() {
        return category;
    }

    public void setCategory(ProjectCategory category) {
        this.category = category;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
