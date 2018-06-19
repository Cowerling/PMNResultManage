$(document).ready(function () {
    $("#creator_list_table").createDataTable("list/creator", true);
});

$(document).ready(function () {
    $(".select2").select2();

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