$(document).ready(function () {
    $(".menu-attachment").activeMenu();
    $(".menu-attachment-upload").activeMenu();
});

$(document).ready(function () {
    $(".select2").select2({
        language: "zh-CN"
    });
});

$(document).ready(function () {
    $("#file").fileinput({
        uploadUrl: "upload",
        language: "zh",
        uploadExtraData: function (previewId, index) {
            return {
                _csrf : $("input[name='_csrf']").val(),
                projectTag: $("#project_list").val(),
                remark: $("#attachment_remark").val()
            };
        }
    }).fileinput("lock");
});