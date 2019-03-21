package com.cowerling.pmn.exception;

public final class ExceptionMessage {
    private ExceptionMessage() {
    }

    public final static String REGISTER_SESSION_AUTHENTICATION = "退出当前用户再注册";

    public final static String DATA_UPLOAD_PROJECT_MEMBER = "无法在该项目中上传数据";
    public final static String DATA_UPLOAD_DATA_CATEGORY = "数据文件格式不符合要求";
    public final static String DATA_UPLOAD_UNKNOWN = "未知错误";
    public final static String DATA_PARSE_CONTENT_INCONFORMITY = "数据文件内容不符合要求";
    public final static String DATA_EDIT_SAVE = "数据保存失败，内容可能不符合要求";
    public final static String DATA_AUTHORITY_MODIFY = "权限修改失败";

    public final static String ATTACHMENT_UPLOAD_PROJECT = "无法在该项目中上传附件";
}