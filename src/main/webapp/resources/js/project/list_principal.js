$(document).ready(function () {
    const columns = [
        {
            name: "name",
            data: "name"
        },
        {
            name: "category",
            data: "category"
        },
        {
            name: "create_time",
            data: "create_time"
        },
        {
            name: "status",
            data: "status"
        }
    ];

    $("#principal_list_table").on("draw.dt", function () {
        $(this).initProjectTools();
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "list/principal",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#principal_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count;
                result.recordsFiltered = result.projects.length;

                for (let i =0, length = result.projects.length; i < length; i++) {
                    result.projects[i].name = $.createProjectName(result.projects[i].name, result.projects[i].tag);
                    result.projects[i].create_time = $(new Date(result.projects[i].createTime)).dateFormat("yyyy-MM-dd hh:mm:ss");
                    result.projects[i].status = $.createProjectStatus(result.projects[i].status);
                }

                return result.projects;
            }
        },
        language: $.listTableLanguage
    });
});

$(document).ready(function () {
    $("#project_search_modal").find("div.principal-select").hide();

    $("a[data-toggle='tab']").on("shown.bs.tab", function (event) {
        if (event.target.hash == "#principal_list_tab") {
            $("#project_search_modal").find("div.principal-select").hide();
        }
    })
});