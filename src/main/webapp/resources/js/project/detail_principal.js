$(document).ready(function () {
    $("#start_project").click(function (event) {
        event.preventDefault();

        $("#start_project").parent("form").submit();
    });
});

$(document).ready(function () {
    $("#modal_select_user").createDetailUserSelectModal("participator", true, "\u9009\u62e9\u53c2\u4e0e\u4eba", "settings/memberAdd", "member");

    $(".member-remove").click(function (event) {
        event.preventDefault();

        $("#modal_remove_member").find("input[name='member']").val("[\"" + $(this).prev(".member-name").val() + "\"]");
        $("#modal_remove_member").modal("show");
    });
});