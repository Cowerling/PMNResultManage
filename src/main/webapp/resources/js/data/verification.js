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
                    result.dataRecords[i].operation =
                        "<span class='label table-status label-primary' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white' href='../data/view/" + result.dataRecords[i].tag + "'>\u67e5\u9605</a>" +
                        "</span>" +
                        "<span class='label table-status label-success' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white verification-qualified' href='" + result.dataRecords[i].tag + "'><i class='fa fa-check-circle-o icon-right'></i>\u901a\u8fc7</a>" +
                        "</span>" +
                        "<span class='label table-status label-danger' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white verification-unqualified' href='" + result.dataRecords[i].tag + "'><i class='fa fa-times-circle-o icon-right'></i>\u4e0d\u901a\u8fc7</a>" +
                        "</span>"
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });

    $("#qualified_list_table").on("draw.dt", function () {
        $(".authority-edit").click(function (event) {
            event.preventDefault();

            $("#authority_edit_modal").modal("show");

            //alert($(this).attr("href"));
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
                    result.dataRecords[i].operation =
                        "<span class='label table-status label-primary' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white' href='../data/view/" + result.dataRecords[i].tag + "'>\u67e5\u9605</a>" +
                        "</span>" +
                        "<span class='label table-status label-success' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white authority-edit' href='" + result.dataRecords[i].tag + "'><i class='fa fa-wrench icon-right'></i>\u6743\u9650</a>" +
                        "</span>"
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
                    result.dataRecords[i].operation =
                        "<span class='label table-status label-primary' style='font-size: 100%; margin-right: 3px;'>" +
                        "<a class='text-white' href='../data/view/" + result.dataRecords[i].tag + "'>\u67e5\u9605</a>" +
                        "</span>"
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });
});