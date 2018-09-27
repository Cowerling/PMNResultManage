$(document).ready(function () {
    $(".menu-data").activeMenu();
    $(".menu-data-upload").activeMenu();
});

$(document).ready(function () {
    $(".select2").select2({
        language: "zh-CN"
    });
});

$(document).ready(function () {
    $("#project_list").change(function (event) {
        $("#project_tag").val($(this).val());
    });

    $("#project_search").click(function (event) {
        $.get("../project/list/participator/summary", {
            search: JSON.stringify({
                name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
                category: $("#search_project_category").val(),
                status: ["PROGRESS"]
            })
        }, function (result) {
            $("#project_list").empty();

            for (let i = 0, length = result.length; i < length; i++) {
                $("#project_list").append("<option value='" + result[i].tag + "'>" + result[i].name + "</option>");
            }

            if (result.length != 0) {
                $("#file").fileinput("unlock");
            }
        });
    });

    $("#templet_list").change(function (event) {
        $("#templet_download > a").attr("href", $(this).val() != "" ? CONSTANT.DATA_TEMPLET_FILE_URL + $(this).val() + ".xls" : "");
    });

    $("#templet_download").click(function (event) {
        $("#templet_download > a")[0].click();
    });

    $("#file").fileinput({
        uploadUrl: "upload",
        language: "zh",
        previewFileIconSettings: {
            xls: "<i class='fa fa-file-excel-o text-success'></i>",
            xlsx: "<i class='fa fa-file-excel-o text-success'></i>"
        },
        uploadExtraData: function (previewId, index) {
            return {
                _csrf : $("input[name='_csrf']").val(),
                projectTag: $("#project_list").val()
            };
        }
    }).fileinput("lock");
});