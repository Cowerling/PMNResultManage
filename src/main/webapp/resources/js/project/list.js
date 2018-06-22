$(document).ready(function () {
    $(".menu-project").activeMenu();
    $(".menu-project-list").activeMenu();
});

$(document).ready(function () {
    $(".date-range").createDateRangePicker();

    $(".select2").select2();

    $("#modal_select_user").createMultiUsersSelectModal();

    $("#clear_search").hide().click(function (event) {
        $(".data-table").find(".search-condition").val("");
        $(".data-table").DataTable().ajax.reload();

        $("#project_search_modal").find("input").not(".date-range").val("");
        $("#project_search_modal").find(".date-range").data("daterangepicker").setStartDate($(new Date()).dateFormat("yyyy-MM-dd hh:mm:ss"));
        $("#project_search_modal").find(".date-range").data("daterangepicker").setEndDate($(new Date()).dateFormat("yyyy-MM-dd hh:mm:ss"));
        $("#project_search_modal").find("select").val("").trigger("change");
        $("#project_search_modal").find("textarea").val("");

        $(this).hide();
    });
});