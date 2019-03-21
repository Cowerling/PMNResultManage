package com.cowerling.pmn.domain.attachment;

public enum AttachmentAuthority {
    BASIS("概况"), DOWNLOAD("下载"), DELETE("删除");

    private String description;

    AttachmentAuthority(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String format() {
        return "data-record-authority-" + name().toLowerCase().replace('_', '-');
    }
}
