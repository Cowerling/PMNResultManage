package com.cowerling.pmn.domain.attachment;

import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;

@Alias("attachment")
public class Attachment {
    @JsonIgnore
    private Long id;
    private String name;
    private String file;
    private Project project;
    private User uploader;
    private Date uploadTime;
    private String remark;
    private List<AttachmentAuthority> authorities;
    private String tag;

    public Attachment() {
        uploadTime = new Date();
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<AttachmentAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AttachmentAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
