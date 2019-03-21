$(document).ready(function () {
    $("#project_search").click(function (event) {
        $("#project_list").empty();

        $.get("../project/list/principal/summary", {
            search: JSON.stringify({
                name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
                category: $("#search_project_category").val(),
                status: ["PROGRESS"]
            })
        }, function (result) {
            for (let i = 0, length = result.length; i < length; i++) {
                $("#project_list").append("<option value='" + result[i].tag + "'>" + result[i].name + "</option>");
            }

            if (result.length != 0) {
                $("#file").fileinput("unlock");
            }
        });

        $.get("../project/list/participator/summary", {
            search: JSON.stringify({
                name: $("#search_project_name").val() != "" ? $("#search_project_name").val().split("#") : [],
                category: $("#search_project_category").val(),
                status: ["PROGRESS"]
            })
        }, function (result) {
            for (let i = 0, length = result.length; i < length; i++) {
                $("#project_list").append("<option value='" + result[i].tag + "'>" + result[i].name + "</option>");
            }

            if (result.length != 0) {
                $("#file").fileinput("unlock");
            }
        });
    });
});