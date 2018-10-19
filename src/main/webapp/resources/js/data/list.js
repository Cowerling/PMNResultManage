$(document).ready(function () {
    $(".menu-data").activeMenu();
    $(".menu-data-list").activeMenu();
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
            name: "status",
            data: "status"
        },
        {
            name: "operation",
            data: "operation"
        }
    ];

    $("#data_list_table").on("draw.dt", function () {
        $(this).find("a.delete").click(function (event) {
            event.preventDefault();
            event.stopPropagation();

            $("#record_remove_modal").find(".record-tag").val($(this).attr("href"));
            $("#record_remove_modal").modal("show");
        });
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: CONSTANT.DATA_RECORD_LIST_URL,
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#data_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count;
                result.recordsFiltered = result.dataRecords.length;

                for (let i =0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.dataRecords[i].status = $.createDataRecordStatus(result.dataRecords[i].status);
                    result.dataRecords[i].operation = $.createDataRecordOperation(result.dataRecords[i].authorities, result.dataRecords[i].tag);
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });
});

$(document).ready(function () {
    $("#record_remove_modal").find(".confirm-remove").click(function (event) {
        $.post($("#record_remove_modal").find(".record-tag").val(), {
            _csrf: $("#record_remove_modal").find("input[name='_csrf']").val()
        }, function (result) {
            $("#data_list_table").DataTable().ajax.reload();
        });

        $("#record_remove_modal").modal("hide");
    });
});

$(document).ready(function () {
    $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
        userGrade: "participator"
    }, true, function (event) {
        $("#data_record_search_modal").find("input.uploader-alias").val($("#select_user_modal").getSelectedUserAliases().join(","));
        $("#data_record_search_modal").find("input.uploader-name").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
    }, "\u9009\u62e9\u4e0a\u4f20\u4eba");
});

$(document).ready(function () {
    let $data_record_search_modal = $("#data_record_search_modal");

    $data_record_search_modal.find("button.ok").click(function (event) {
        $("#data_list_table").find(".search-condition").val(JSON.stringify({
            name: $data_record_search_modal.find("input.data-record-name").val() != "" ? $data_record_search_modal.find("input.data-record-name").val().split("#") : [],
            projectName: $data_record_search_modal.find("input.project-name").val() != "" ? $data_record_search_modal.find("input.project-name").val().split("#") : [],
            uploaderName: $data_record_search_modal.find("input.uploader-name").val() != "" ? JSON.parse($data_record_search_modal.find("input.uploader-name").val()) : [],
            uploadTime: $data_record_search_modal.find("input.upload-time").val().split(" - "),
            status: $data_record_search_modal.find("select.data-record-status").val()
        }));

        $(".data-table").DataTable().ajax.reload();
    });
});