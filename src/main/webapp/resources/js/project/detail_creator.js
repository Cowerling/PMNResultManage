$(document).ready(function () {
    $("#modal_select_user").createUserSelectModal("\u9009\u62e9\u7ba1\u7406\u4eba", "settings/manager", "manager");
});

$(document).ready(function () {
    $.extend($.validator.messages, {
        maxlength: $.validator.format("\u6700\u591a\u53ef\u4ee5\u8f93\u5165{0}\u4e2a\u5b57\u7b26") //最多可以输入{0}个字符
    });

    $("#settings_remark").validate({
        errorPlacement: function(error, element) {
            error.appendTo(element.parent()).addClass("text-red");
        }
    });
});
