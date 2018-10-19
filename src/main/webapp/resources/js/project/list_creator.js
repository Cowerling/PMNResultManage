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

    $("#creator_list_table").on("draw.dt", function () {
        $(this).find(".table-tools").append("<a class='project-delete' href='#'><i class='fa fa-trash-o table-delete-tool'></i></a>");

        $(this).find(".project-delete").click(function (event) {
            event.preventDefault();
            let project_tag = $(this).prev().attr("href");

            $(".project-remove-hint").find("form").attr("action", CONSTANT.PROJECT_REMOVE_URL);
            $(".project-remove-hint").find("input.project-tag").val(project_tag);

            $(".project-remove-hint").modal("show")
        });

        $(this).initProjectTools();
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "list/creator",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters),
                    search: $("#creator_list_table").find(".search-condition").val()
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
    $("#project_add_form").validate({
        errorPlacement: function(error, element) {
            error.appendTo(element.parent()).addClass("text-red");
        }
    });
});

$(document).ready(function () {
    let $project_search_modal = $("#project_search_modal");

    $project_search_modal.find("button.project-manager-select").click(function (event) {
        $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
            userGrade: "manager"
        }, true, function (event) {
            $project_search_modal.find("input.project-manager-alias").val($("#select_user_modal").getSelectedUserAliases().join(","));
            $project_search_modal.find("input.project-manager-name").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
        }, "\u9009\u62e9\u7ba1\u7406\u4eba").modal("show")
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
            manager: $project_search_modal.find("input.project-manager-name").val() != "" ? JSON.parse($project_search_modal.find("input.project-manager-name").val()) : [],
            principal: $project_search_modal.find("input.project-principal-name").val() != "" ? JSON.parse($project_search_modal.find("input.project-principal-name").val()) : [],
            remark: $project_search_modal.find("textarea.project-remark").val(),
            status: $project_search_modal.find("select.project-status").val()
        }));

        $(".data-table").DataTable().ajax.reload();

        $("#project_search_modal").modal("hide");
        $("#clear_search").show();
    });
});