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
});