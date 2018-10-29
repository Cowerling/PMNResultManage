package com.cowerling.pmn.domain.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ibatis.type.Alias;

@Alias("document")
public class Document {
    @JsonIgnore
    private Long id;
    private String name;
    private String brief;
    private String file;
    private String tag;

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

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
