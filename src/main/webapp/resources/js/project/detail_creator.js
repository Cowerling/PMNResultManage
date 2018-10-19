$(document).ready(function () {
    $("#select_user_modal").siblings("form").attr("action", "settings/manager");
    $("#select_user_modal").siblings("form").append("<input type='hidden' name='manager' />")

    $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
        userGrade: "manager"
    }, false, function (event) {
        let selected_user_names = $("#select_user_modal").getSelectedUserNames();
        if (selected_user_names.length == 1) {
            $("#select_user_modal").siblings("form").find("input[name=manager]").val($("#select_user_modal").getSelectedUserNames()[0]);
            $("#select_user_modal").siblings("form").submit();
        }
    }, "\u9009\u62e9\u7ba1\u7406\u4eba");
});

$(document).ready(function () {
    $("#settings_remark").validate();
});
