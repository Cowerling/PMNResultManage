$(document).ready(function () {
    let local_city = new T.LocalCity();

    local_city.location(function (result) {
        $("#location").text(result.cityName);
    });

    $(".select2").select2();

    $("#inputBirthday").datepicker({
        format: "yyyy-mm-dd",
        autoclose: true
    });

    $("[data-mask]").inputmask();

    $("input").iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%'
    });

    $.extend($.validator.messages, {
        required: "\u5fc5\u586b",    //必填
        email: "\u90ae\u7bb1\u683c\u5f0f\u9519\u8bef",  //邮箱格式错误
        equalTo: "\u5bc6\u7801\u5fc5\u987b\u76f8\u540c", //密码必须相同
        maxlength: $.validator.format("\u6700\u591a\u53ef\u4ee5\u8f93\u5165{0}\u4e2a\u5b57\u7b26"), //最多可以输入{0}个字符
        minlength: $.validator.format("\u6700\u5c11\u8981\u8f93\u5165{0}\u4e2a\u5b57\u7b26")   //最少要输入{0}个字符
    });

    $("#settingForm").validate({
        errorPlacement: function(error, element) {
            error.appendTo(element.parent()).addClass("text-red");
        }
    });

    $("#securityForm").validate({
        errorPlacement: function(error, element) {
            error.appendTo(element.parent()).addClass("text-red");
        }
    });
});

$(document).ready(function () {
    $("div.image-preview").imageSelect();
});