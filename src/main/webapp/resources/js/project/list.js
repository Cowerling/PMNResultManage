$(document).ready(function () {
    $(".menu-project").activeMenu();
    $(".menu-project-list").activeMenu();
});

$(document).ready(function () {
    $(".date-range").createDateRangePicker();

    $(".multi-select2").select2();

    $("#modal_select_user").createMultiUsersSelectModal();

    $("#search_project_creator_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u521b\u5efa\u4eba");    //选择创建人
        $("#modal_select_user").find(".user-grade").val("creator");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_creator").val($("#modal_select_user").find(".input-user").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project_manager_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u7ba1\u7406\u4eba");    //选择管理人
        $("#modal_select_user").find(".user-grade").val("manager");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_manager").val($("#modal_select_user").find(".input-user").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project_principal_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u8d1f\u8d23\u4eba");    //选择负责人
        $("#modal_select_user").find(".user-grade").val("principal");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_principal").val($("#modal_select_user").find(".input-user").val());
        });

        $("#modal_select_user").modal("show");
    });

    $("#search_project").click(function (event) {
        $("#search_project_query").val(JSON.stringify({
            name: $("#search_project_name").val(),
            category: $("#search_project_category").val(),
            creator: $("#search_project_creator").val(),
            createTime: $("#search_project_create_time").val(),
            manager: $("#search_project_manager").val(),
            principal: $("#search_project_principal").val(),
            remark: $("#search_project_remark").val(),
            status: $("#search_project_status").val()
        }));

        alert($("#search_project_query").val());
    });
});