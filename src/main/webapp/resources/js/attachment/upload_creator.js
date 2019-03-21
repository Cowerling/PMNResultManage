$(document).ready(function () {
    $("#project_search").click(function (event) {
        $.get("../project/list/creator/summary", {
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
});