$(document).ready(function () {
    $("#creator_list_table").createDataTable("list/creator", true);
});

$(document).ready(function () {
    $.extend($.validator.messages, {
        required: "\u5fc5\u586b",    //必填
        maxlength: $.validator.format("\u6700\u591a\u53ef\u4ee5\u8f93\u5165{0}\u4e2a\u5b57\u7b26") //最多可以输入{0}个字符
    });

    $("#project_add_form").validate({
        errorPlacement: function(error, element) {
            error.appendTo(element.parent()).addClass("text-red");
        }
    });
});

$(document).ready(function () {
    $("#search_project_manager_select").click(function (event) {
        $("#modal_select_user").find(".modal-title").text("\u9009\u62e9\u7ba1\u7406\u4eba");    //选择管理人
        $("#modal_select_user").find(".user-grade").val("manager");

        $("#modal_select_user").one("hide.bs.modal", function (event) {
            $("#search_project_manager").val($("#modal_select_user").find(".input-user").val());
            $("#search_project_manager_alias").val($("#modal_select_user").find(".input-user-alias").val());
        });

        $("#modal_select_user").modal("show");
    });

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
        $(".data-table").find(".search-condition").val(JSON.stringify({
            name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
            category: $("#search_project_category").val(),
            createTime: $("#search_project_create_time").val().split(" - "),
            manager: $("#search_project_manager").val() != "" ? $("#search_project_manager").val().split(";") : [],
            principal: $("#search_project_principal").val() != "" ? $("#search_project_principal").val().split(";") : [],
            remark: $("#search_project_remark").val(),
            status: $("#search_project_status").val()
        }));

        $(".data-table").DataTable().ajax.reload();
        $("#project_search_modal").modal("hide");

        $("#clear_search").show();
    });
});