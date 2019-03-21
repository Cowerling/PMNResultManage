$(document).ready(function () {
    $(".menu-attachment").activeMenu();
    $(".menu-attachment-list").activeMenu();
});

$(document).ready(function () {
    $(".date-range").createDateRangePicker();

    $(".select2").select2({
        language: "zh-CN"
    });
});

$(document).ready(function () {
    const columns = [
        {
            name: "name",
            data: "name"
        },
        {
            name: "projectName",
            data: "projectName"
        },
        {
            name: "uploaderAlias",
            data: "uploaderAlias"
        },
        {
            name: "uploadTime",
            data: "uploadTime"
        },
        {
            name: "operation",
            data: "operation"
        }
    ];

    $("#attachment_list_table").on("draw.dt", function () {
        $(this).find("a.delete").click(function (event) {
            event.preventDefault();
            event.stopPropagation();

            $("#attachment_remove_modal").find(".attachment-tag").val($(this).attr("href"));
            $("#attachment_remove_modal").modal("show");
        });
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: CONSTANT.ATTACHMENT_LIST_URL,
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#attachment_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count;
                result.recordsFiltered = result.count;

                for (let i =0, length = result.attachments.length; i < length; i++) {
                    result.attachments[i].projectName = result.attachments[i].project.name;
                    result.attachments[i].uploaderAlias = result.attachments[i].uploader.alias;
                    result.attachments[i].uploadTime = $(new Date(result.attachments[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.attachments[i].operation = $.createAttachmentOperation(result.attachments[i].authorities, result.attachments[i].tag);
                }

                return result.attachments;
            }
        },
        language: $.listTableLanguage
    });
});

$(document).ready(function () {
    $("#attachment_remove_modal").find(".confirm-remove").click(function (event) {
        $.post($("#attachment_remove_modal").find(".attachment-tag").val(), {
            _csrf: $("#attachment_remove_modal").find("input[name='_csrf']").val()
        }, function (result) {
            $("#attachment_list_table").DataTable().ajax.reload();
        });

        $("#attachment_remove_modal").modal("hide");
    });
});

$(document).ready(function () {
    $("#select_user_modal").selectUserModal(
        CONSTANT.DEPARTMENT_USERS_URL,
        {},
        true,
        function (event) {
            $("#attachment_search_modal").find("input.uploader-alias").val($("#select_user_modal").getSelectedUserAliases().join(","));
            $("#attachment_search_modal").find("input.uploader-name").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
        },
        "\u9009\u62e9\u4e0a\u4f20\u4eba");
});

$(document).ready(function () {
    let $attachment_search_modal = $("#attachment_search_modal");

    $attachment_search_modal.find("button.ok").click(function (event) {
        $("#attachment_list_table").find(".search-condition").val(JSON.stringify({
            name: $attachment_search_modal.find("input.attachment-name").val() != "" ? $attachment_search_modal.find("input.attachment-name").val().split("#") : [],
            projectName: $attachment_search_modal.find("input.project-name").val() != "" ? $attachment_search_modal.find("input.project-name").val().split("#") : [],
            uploaderName: $attachment_search_modal.find("input.uploader-name").val() != "" ? JSON.parse($attachment_search_modal.find("input.uploader-name").val()) : [],
            uploadTime: $attachment_search_modal.find("input.upload-time").val().split(" - "),
            remark: $attachment_search_modal.find("textarea.attachment-remark").val()
        }));

        $(".data-table").DataTable().ajax.reload();
    });
});