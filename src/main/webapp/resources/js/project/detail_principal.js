$(document).ready(function () {
    $("#start_project").click(function (event) {
        event.preventDefault();

        $("#start_project").parent("form").submit();
    });
});

$(document).ready(function () {
    $("#select_user_modal").siblings("form").attr("action", "settings/memberAdd");
    $("#select_user_modal").siblings("form").append("<input type='hidden' name='member' />")

    $("#select_user_modal").selectUserModal(CONSTANT.DEPARTMENT_USERS_URL, {
        userGrade: "participator"
    }, true, function (event) {
        $("#select_user_modal").siblings("form").find("input[name=member]").val(JSON.stringify($("#select_user_modal").getSelectedUserNames()));
        $("#select_user_modal").siblings("form").submit();
    }, "\u9009\u62e9\u53c2\u4e0e\u4eba");
});

$(document).ready(function () {
    $(".member-remove").click(function (event) {
        event.preventDefault();

        $("#modal_remove_member").find("input[name='member']").val("[\"" + $(this).prev(".member-name").val() + "\"]");
        $("#modal_remove_member").modal("show");
    });
});