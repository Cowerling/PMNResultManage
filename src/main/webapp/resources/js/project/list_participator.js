$(document).ready(function () {
    $("#participator_list_table").createDataTable("list/participator", false);
});

$(document).ready(function () {
    $("a[data-toggle='tab']").on("shown.bs.tab", function (event) {
        if (event.target.hash == "#participator_list_tab") {
            $("div.principal-select").show();
        }
    })
});

$(document).ready(function () {
    $("#search_project_principal_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u8d1f\u8d23\u4eba");    //选择负责人
        $("#modal_select_user").find(".user-grade").val("principal");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_principal").val($("#modal_select_user").find(".input-user").val());
            $("#search_project_principal_alias").val($("#modal_select_user").find(".input-user-alias").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project").click(function (event) {
        $("#participator_list_table.data-table").find(".search-condition").val(JSON.stringify({
            name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
            category: $("#search_project_category").val(),
            createTime: $("#search_project_create_time").val().split(" - "),
            creator: $("#search_project_creator").val() != "" ? $("#search_project_creator").val().split(";") : [],
            manager: $("#search_project_manager").val() != "" ? $("#search_project_manager").val().split(";") : [],
            principal: $("#search_project_principal").val() != "" ? $("#search_project_principal").val().split(";") : [],
            remark: $("#search_project_remark").val(),
            status: $("#search_project_status").val()
        }));

        $("#participator_list_table.data-table").DataTable().ajax.reload();

        $("#project_search_modal").modal("hide");
        $("#clear_search").show();
    });
});