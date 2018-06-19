$(document).ready(function () {
    $.extend($.validator.messages, {
        required: "\u5fc5\u586b",   //必填
        equalTo: "\u5bc6\u7801\u5fc5\u987b\u76f8\u540c" //密码必须相同
    });

    $("form").validate({
        errorPlacement: function(error, element) {
            if (element.hasClass("agree")) {
                error.appendTo(element.parent().parent().parent()).addClass("text-red");
            } else {
                error.appendTo(element.parent()).addClass("text-red");
            }
        }
    });
});