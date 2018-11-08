$(document).ready(function () {
    $.get("project/list/participator/summary", {
        search: JSON.stringify({
            status: ["PROGRESS", "FINISH"],
            finishTime: [$(new Date(0)).dateFormat("yyyy-MM-dd hh:mm:ss"), $(new Date()).dateFormat("yyyy-MM-dd hh:mm:ss")],
        })
    }, function (projects) {
        $("#calendar").fullCalendar("addEventSource", $.createProjectCalendarEvent(projects));
    });
});
