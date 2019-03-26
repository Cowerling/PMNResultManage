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

    $("#manager_list_table").on("draw.dt", function () {
        $(this).initProjectTools();
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "list/manager",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#manager_list_table").find(".search-condition").val()
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.count;
                result.recordsFiltered = result.count;

                for (let i =0, length = result.projects.length; i < length; i++) {
                    result.projects[i].name = $.createProjectName(result.projects[i].name, result.projects[i].tag);
                    result.projects[i].category = $.createProjectCategory(result.projects[i].category);
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
    let $project_search_modal = $("#project_search_modal");

    $project_search_modal.find("button.project-creator-select").click(function (event) {
        $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
            userGrade: "creator"
        }, true, function (event) {
            $project_search_modal.find("input.project-creator-alias").val($("#select_user_modal").getSelectedUserAliases().join(","));
            $project_search_modal.find("input.project-creator-name").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
        }, "\u9009\u62e9\u521b\u5efa\u4eba").modal("show")
    });

    $project_search_modal.find("button.project-principal-select").click(function (event) {
        $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
            userGrade: "principal"
        }, true, function (event) {
            $project_search_modal.find("input.project-principal-alias").val($("#select_user_modal").getSelectedUserAliases().join(","));
            $project_search_modal.find("input.project-principal-name").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
        }, "\u9009\u62e9\u8d1f\u8d23\u4eba").modal("show");
    });

    $project_search_modal.find("button.ok").click(function (event) {
        $(".data-table").find(".search-condition").val(JSON.stringify({
            name: $project_search_modal.find("input.project-name").val() != "" ? $project_search_modal.find("input.project-name").val().split("#") : [],
            category: $project_search_modal.find("select.project-category").val(),
            createTime: $project_search_modal.find("input.project-create-time").val().split(" - "),
            creator: $project_search_modal.find("input.project-creator-name").val() != "" ? JSON.parse($project_search_modal.find("input.project-creator-name").val()) : [],
            principal: $project_search_modal.find("input.project-principal-name").val() != "" ? JSON.parse($project_search_modal.find("input.project-principal-name").val()) : [],
            remark: $project_search_modal.find("textarea.project-remark").val(),
            status: $project_search_modal.find("select.project-status").val()
        }));

        $(".data-table").DataTable().ajax.reload();

        $("#project_search_modal").modal("hide");
        $("#clear_search").show();
    });
});