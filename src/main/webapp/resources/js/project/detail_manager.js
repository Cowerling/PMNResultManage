$(document).ready(function () {
    $("#select_user_modal").siblings("form").attr("action", "settings/principal");
    $("#select_user_modal").siblings("form").append("<input type='hidden' name='principal' />")

    $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
        userGrade: "principal"
    }, false, function (event) {
        let selected_user_names = $("#select_user_modal").getSelectedUserNames();
        if (selected_user_names.length == 1) {
            $("#select_user_modal").siblings("form").find("input[name=principal]").val($("#select_user_modal").getSelectedUserNames()[0]);
            $("#select_user_modal").siblings("form").submit();
        }
    }, "\u9009\u62e9\u8d1f\u8d23\u4eba");
});

$(document).ready(function () {
    $("#verification_setting").find(".submit").click(function (event) {
        $(this).siblings("input[name=managerAdopt]").val("true");
        $(this).parent("form").submit();
    });

    $("#verification_setting").find(".reject").click(function (event) {
        $(this).siblings("input[name=managerAdopt]").val("false");
        $(this).parent("form").submit();
    });
});
