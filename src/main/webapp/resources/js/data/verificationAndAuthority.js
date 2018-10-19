$(document).ready(function () {
    $(".menu-data").activeMenu();
    $(".menu-data-verification").activeMenu();
});

$(document).ready(function () {
    $.extend($.validator.messages, {
        required: "\u5fc5\u586b",    //必填
        maxlength: $.validator.format("\u6700\u591a\u53ef\u4ee5\u8f93\u5165{0}\u4e2a\u5b57\u7b26") //最多可以输入{0}个字符
    });

    $(".select2").select2({
        language: "zh-CN"
    });

    $("input[type='checkbox']").iCheck({
        checkboxClass: "icheckbox_minimal-blue",
        radioClass: "iradio_minimal-blue"
    });

    $(".date-range").createDateRangePicker();
});

$(document).ready(function () {
    $("#unaudited_list_table").find(".search-condition").val(JSON.stringify({
        status: ["UNAUDITED"],
        verificationOnly: true
    }));

    $("#qualified_list_table").find(".search-condition").val(JSON.stringify({
        status: ["QUALIFIED"],
        verificationOnly: true
    }));

    $("#unqualified_list_table").find(".search-condition").val(JSON.stringify({
        status: ["UNQUALIFIED"],
        verificationOnly: true
    }));

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

    $("#unaudited_list_table").on("draw.dt", function () {
        $(".verification-qualified").click(function (event) {
            event.preventDefault();

            $("#verification_modal").find(".qualified").show();
            $("#verification_modal").find(".unqualified").hide();
            $("#verification_modal").find("button:submit").removeClass("btn-danger").addClass("btn-success");
            $("#verification_modal").find(".data-record-tag").val($(this).attr("href"));
            $("#verification_modal").find(".data-record-status").val("qualified");
            $("#verification_modal").modal("show");
        });

        $(".verification-unqualified").click(function (event) {
            event.preventDefault();

            $("#verification_modal").find(".unqualified").show();
            $("#verification_modal").find(".qualified").hide();
            $("#verification_modal").find("button:submit").removeClass("btn-success").addClass("btn-danger");
            $("#verification_modal").find(".data-record-tag").val($(this).attr("href"));
            $("#verification_modal").find(".data-record-status").val("unqualified");
            $("#verification_modal").modal("show");
        });
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "record/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#unaudited_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count
                result.recordsFiltered = result.dataRecords.length;

                for (var i =0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].name = result.dataRecords[i].name;
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.dataRecords[i].operation = $.createDataRecordOperation(
                        result.dataRecords[i].authorities,
                        result.dataRecords[i].tag,
                        ["DOWNLOAD", "EDIT", "DELETE"],
                        "<span class='label table-status label-success' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white verification-qualified' href='" + result.dataRecords[i].tag + "'><i class='fa fa-check-circle-o icon-right'></i>\u901a\u8fc7</a>" +
                        "</span>" +
                        "<span class='label table-status label-danger' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white verification-unqualified' href='" + result.dataRecords[i].tag + "'><i class='fa fa-times-circle-o icon-right'></i>\u4e0d\u901a\u8fc7</a>" +
                        "</span>");
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });

    $("#qualified_list_table").on("draw.dt", function () {
        $(".authority-edit").click(function (event) {
            event.preventDefault();
            let dataRecordTag = $(this).attr("href");

            $("#authority_edit_modal").find("input.data-record-tag").val(dataRecordTag);

            $("#authority_edit_select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_PROJECT_USERS_URL, {
                dataRecordTag: dataRecordTag
            }, false, function (event) {
                let selected_user_names = $("#authority_edit_select_user_modal").getSelectedUserNames(),
                    selected_user_aliases = $("#authority_edit_select_user_modal").getSelectedUserAliases();

                if (selected_user_names.length == 1) {
                    $("#authority_edit_modal").find("input.user-alias").val(selected_user_aliases[0]);
                    $("#authority_edit_modal").find("input.user").val(selected_user_names[0]);

                    $.get(CONSTANT.DATA_AUTHORITY_URL + $("#authority_edit_modal").find("input.data-record-tag").val(), {
                        userName: $("#authority_edit_modal").find("input.user").val()
                    }, function (result) {
                        $("#authority_edit_modal").find("select.authorities").val(result).trigger("change");
                    });
                }
            }, "\u9009\u62e9\u7528\u6237");

            $("#authority_edit_modal").modal("show");
        });
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "record/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#qualified_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count
                result.recordsFiltered = result.dataRecords.length;

                for (var i =0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].name = result.dataRecords[i].name;
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.dataRecords[i].operation = $.createDataRecordOperation(
                        result.dataRecords[i].authorities,
                        result.dataRecords[i].tag,
                        ["DOWNLOAD", "EDIT", "DELETE"],
                        "<span class='label table-status label-success' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white authority-edit' href='" + result.dataRecords[i].tag + "'><i class='fa fa-wrench icon-right'></i>\u6743\u9650</a>" +
                        "</span>");
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });

    $("#unqualified_list_table").DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "record/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#unqualified_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count
                result.recordsFiltered = result.dataRecords.length;

                for (var i =0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].name = result.dataRecords[i].name;
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.dataRecords[i].operation = $.createDataRecordOperation(
                        result.dataRecords[i].authorities,
                        result.dataRecords[i].tag,
                        ["DOWNLOAD", "EDIT", "DELETE"]);
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });
});

$(document).ready(function () {
    $("#authority_edit_modal").on("show.bs.modal", function (event) {
        $(this).find("input.user-alias").val(undefined);
        $(this).find("input.user").val(undefined);
        $(this).find("select.authorities").val(null).trigger("change");
        $(this).find("input.all-user").iCheck("uncheck");
    });

    $("#authority_edit_modal").find("button.apply").click(function (event) {
        $.post(CONSTANT.DATA_AUTHORITY_URL + $("#authority_edit_modal").find("input.data-record-tag").val(), {
            _csrf: $("#authority_edit_modal").find("input[name='_csrf']").val(),
            userName: $("#authority_edit_modal").find("input.user").val(),
            allUser: $("#authority_edit_modal").find("input.all-user").is(":checked"),
            authorities: $("#authority_edit_modal").find("select.authorities").val()
        }, function (result) {
            $("#authority_edit_result_modal").find("h4.result-success").show();
            $("#authority_edit_result_modal").find("h4.result-failed").hide();
            $("#authority_edit_result_modal").modal("show");
        }).fail (function (result) {
            $("#authority_edit_result_modal").find("h4.result-failed").show();
            $("#authority_edit_result_modal").find("h4.result-success").hide();
            $("#authority_edit_result_modal").modal("show");
        });
    });

    $("#authority_edit_modal").find("button.ok").click(function (event) {
        $("#authority_edit_modal").find("button.apply").click();
    });

    $("#authority_edit_modal").find("input.all-user").on("ifChecked", function (event) {
        $("#authority_edit_modal").find("button.user-select").attr("disabled", "disabled");
    });

    $("#authority_edit_modal").find("input.all-user").on("ifUnchecked", function (event) {
        $("#authority_edit_modal").find("button.user-select").removeAttr("disabled");
    });
});

$(document).ready(function () {
    $("#data_record_search_select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
        userGrade: "participator"
    }, true, function (event) {
        $("#data_record_search_modal").find("input.uploader-alias").val($("#data_record_search_select_user_modal").getSelectedUserAliases().join(","));
        $("#data_record_search_modal").find("input.uploader-name").val(JSON.stringify($("#data_record_search_select_user_modal").getSelectedUserNames()));
    }, "\u9009\u62e9\u4e0a\u4f20\u4eba");
});

$(document).ready(function () {
    let $data_record_search_modal = $("#data_record_search_modal");

    $data_record_search_modal.find("button.ok").click(function (event) {
        let search_condition = {
            name: $data_record_search_modal.find("input.data-record-name").val() != "" ? $data_record_search_modal.find("input.data-record-name").val().split("#") : [],
            projectName: $data_record_search_modal.find("input.project-name").val() != "" ? $data_record_search_modal.find("input.project-name").val().split("#") : [],
            uploaderName: $data_record_search_modal.find("input.uploader-name").val() != "" ? JSON.parse($data_record_search_modal.find("input.uploader-name").val()) : [],
            uploadTime: $data_record_search_modal.find("input.upload-time").val().split(" - ")
        };

        $("#unaudited_list_table").find(".search-condition").val(JSON.stringify($.extend({
            status: ["UNAUDITED"],
            verificationOnly: true
        }, search_condition)));

        $("#qualified_list_table").find(".search-condition").val(JSON.stringify($.extend({
            status: ["QUALIFIED"],
            verificationOnly: true
        }, search_condition)));

        $("#unqualified_list_table").find(".search-condition").val(JSON.stringify($.extend({
            status: ["UNQUALIFIED"],
            verificationOnly: true
        }, search_condition)));

        $(".data-table").DataTable().ajax.reload();
    });
});