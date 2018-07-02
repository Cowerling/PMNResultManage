$(document).ready(function () {
    $("#principal_list_table").createProjectListTable("list/principal", false);
});

$(document).ready(function () {
    $("div.principal-select").hide();

    $("a[data-toggle='tab']").on("shown.bs.tab", function (event) {
        if (event.target.hash == "#principal_list_tab") {
            $("div.principal-select").hide();
        }
    })
});

$(document).ready(function () {
    $("#search_project_creator_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u521b\u5efa\u4eba");    //选择创建人
        $("#modal_select_user").find(".user-grade").val("creator");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_creator").val($("#modal_select_user").find(".input-user").val());
            $("#search_project_creator_alias").val($("#modal_select_user").find(".input-user-alias").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project_manager_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u7ba1\u7406\u4eba");    //选择管理人
        $("#modal_select_user").find(".user-grade").val("manager");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_manager").val($("#modal_select_user").find(".input-user").val());
            $("#search_project_manager_alias").val($("#modal_select_user").find(".input-user-alias").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project").click(function (event) {
        $("#principal_list_table.data-table").find(".search-condition").val(JSON.stringify({
            name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
            category: $("#search_project_category").val(),
            createTime: $("#search_project_create_time").val().split(" - "),
            creator: $("#search_project_creator").val() != "" ? $("#search_project_creator").val().split(";") : [],
            manager: $("#search_project_manager").val() != "" ? $("#search_project_manager").val().split(";") : [],
            remark: $("#search_project_remark").val(),
            status: $("#search_project_status").val()
        }));

        $("#principal_list_table.data-table").DataTable().ajax.reload();

        $("#project_search_modal").modal("hide");
        $("#clear_search").show();
    });
});