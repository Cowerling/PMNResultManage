$(document).ready(function () {
    let $list_table = $("#list_table");

    const columns = [
        {
            name: "name",
            data: "name"
        },
        {
            name: "brief",
            data: "brief"
        },
        {
            name: "operation",
            data: "operation"
        }
    ];

    $list_table.DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "document/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters)
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.documents.length;
                result.recordsFiltered = result.documents.length;

                for (let i = 0, length = result.documents.length; i < length; i++) {
                    result.documents[i].operation = "<span class='label table-status label-primary' style='font-size: 100%; margin-right: 3px;'><a class='text-white' href='document/view/" + result.documents[i].tag + "'>\u67e5\u9605</a></span>";
                }

                return result.documents;
            }
        },
        language: $.listTableLanguage
    });
});