$(document).ready(function () {
    $("#start_project").click(function (event) {
        event.preventDefault();

        $("#start_project").parent("form").submit();
    });
});

$(document).ready(function () {
    $("#modal_select_user").createDetailUserSelectModal("participator", true, "\u9009\u62e9\u53c2\u4e0e\u4eba", "settings/member", "member");
});