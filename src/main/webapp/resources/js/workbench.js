$(document).ready(function () {
    $(".menu-workbench").activeMenu(true);
});

$(document).ready(function () {
    let data = [], labels = [], colors = [];

    $.each($("#project_chart").data("project-statistics"), function (key, value) {
        data.push(value);
        labels.push(key);
        colors.push($.getRandomColor());
    });

    let chart = new Chart($("#project_chart")[0].getContext("2d"), {
        type: "pie",
        data: {
            datasets: [{
                data: data,
                backgroundColor: colors
            }],
            labels: labels
        },
        options: {
            responsive: true
        }
    });
});

$(document).ready(function () {
    $("#calendar").fullCalendar({
        header: {
            left : 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        buttonText: {
            today: 'today',
            month: 'month',
            week: 'week',
            day: 'day'
        },
        events: [
            {
                title: "20181019",
                start: new Date(2018, 9, 19),
                end: new Date(),
                url: "project/qj4NVBsKHHwBkgchKZeYIg==",
                backgroundColor: '#3c8dbc', //Primary (light-blue)
                borderColor: '#3c8dbc' //Primary (light-blue)
            }
        ],
        editable: false,
        droppable: false
    });
});