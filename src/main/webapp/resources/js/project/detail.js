$(document).ready(function () {
    $(".member-list").slimScroll({
        height: "250px"
    });
});

$(document).ready(function () {
    $(".message-remove").click(function (event) {
        event.preventDefault();
        $(this).parent().hide();
    });
});

$(document).ready(function () {
    let $data_list_table = $("#data_list_table");

    $data_list_table.find(".search-condition").val(JSON.stringify({
        projectTag: [$data_list_table.find(".search-condition").val()]
    }));

    const columns = [
        {
            name: "name",
            data: "name"
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

    $data_list_table.DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: CONSTANT.DATA_RECORD_LIST_URL,
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $data_list_table.find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count;
                result.recordsFiltered = result.dataRecords.length;

                for (let i = 0, length = result.dataRecords.length; i < length; i++) {
                    result.dataRecords[i].projectName = result.dataRecords[i].project.name;
                    result.dataRecords[i].uploaderAlias = result.dataRecords[i].uploader.alias;
                    result.dataRecords[i].uploadTime = $(new Date(result.dataRecords[i].uploadTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.dataRecords[i].status = $.createDataRecordStatus(result.dataRecords[i].status);
                    result.dataRecords[i].operation = $.createDataRecordOperation(result.dataRecords[i].authorities, result.dataRecords[i].tag, ["EDIT", "DELETE"]);
                }

                return result.dataRecords;
            }
        },
        language: $.listTableLanguage
    });
});
