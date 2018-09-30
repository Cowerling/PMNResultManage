$(document).ready(function () {
    $(".menu-data").activeMenu();
    $(".menu-data-list").activeMenu();
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
            url: "../data/record/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#data_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count
                result.recordsFiltered = result.dataRecords.length;

                for (let i =0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");

                    let status = "", status_color_style = "";

                    switch (result.dataRecords[i].status) {
                        case "UNAUDITED":
                            status = "\u672a\u5ba1\u6838";
                            status_color_style = "label-info";
                            break;
                        case "QUALIFIED":
                            status = "\u5408\u683c";
                            status_color_style = "label-success";
                            break;
                        case "UNQUALIFIED":
                            status = "\u4e0d\u5408\u683c";
                            status_color_style = "label-danger";
                            break;
                        default:
                            break;
                    }
                    result.dataRecords[i].status = "<span class='lable table-status " + status_color_style + "'>" + status + "</span>";
                    result.dataRecords[i].operation = "";

                    for (let j = 0, operation_length = result.dataRecords[i].authorities.length; j < operation_length; j++) {
                        let authority = result.dataRecords[i].authorities[j];
                        let operation_name = "", operation_style = "";

                        switch (authority) {
                            case "VIEW":
                                operation_name = "\u67e5\u9605";
                                operation_style = "label-primary";
                                break;
                            case "DOWNLOAD":
                                operation_name = "\u4e0b\u8f7d";
                                operation_style = "label-primary";
                                break;
                            case "EDIT":
                                operation_name = "\u7f16\u8f91";
                                operation_style = "label-warning";
                                break;
                            case "DELETE":
                                operation_name = "\u5220\u9664";
                                operation_style = "label-danger";
                                break;
                            default:
                                break;
                        }

                        if (operation_name != "" && operation_style != "") {
                            result.dataRecords[i].operation +=
                                "<span class='label table-status " + operation_style + "' style='font-size: 100%; margin-right: 3px;'>" +
                                "<a class='text-white " + authority.toLowerCase() + "' href='../data/" + authority.toLowerCase() + "/" + result.dataRecords[i].tag + "'>" + operation_name + "</a>" +
                                "</span>"
                        }
                    }
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });

    $("#record_remove_modal").find(".confirm-remove").click(function (event) {
        $.post($("#record_remove_modal").find(".record-tag").val(), {
            _csrf: $("#record_remove_modal").find("input[name='_csrf']").val()
        }, function (result) {
            $("#data_list_table").DataTable().ajax.reload();
        });

        $("#record_remove_modal").modal("hide");
    });
});