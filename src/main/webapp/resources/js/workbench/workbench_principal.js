$(document).ready(function () {
    $.get("project/list/principal/summary", {
        search: JSON.stringify({
            status: ["PROGRESS", "FINISH"],
            finishTime: [$(new Date(0)).dateFormat("yyyy-MM-dd hh:mm:ss"), $(new Date()).dateFormat("yyyy-MM-dd hh:mm:ss")],
        })
    }, function (projects) {
        $("#calendar").fullCalendar("addEventSource", $.createProjectCalendarEvent(projects));
    });
});

$(document).ready(function () {
    let chart = $("#data_record_chart").createDataRecordChart();

    let $data_record_chart_search = $("#data_record_chart_search");

    $data_record_chart_search.find("button.search").click(function (event) {
        let allProjects = [];
        let data = {
            search: JSON.stringify({
                name: $data_record_chart_search.find("input.project-name").val() != "" ? $data_record_chart_search.find("input.project-name").val().split("#") : [],
                createTime: $data_record_chart_search.find("input.create-time").val().split(" - ")
            })
        };

        $.ajax({
            url: "project/list/principal/summary",
            data: data,
            async:false,
            success: function (projects) {
                allProjects = allProjects.concat(projects);
            }
        });

        $.ajax({
            url: "project/list/participator/summary",
            data: data,
            async:false,
            success: function (projects) {
                allProjects = allProjects.concat(projects);
            }
        });

        $(chart).updateDataRecordChart(allProjects, CONSTANT.DATA_RECORD_LIST_SUMMARY_URL);
    }).click();
});