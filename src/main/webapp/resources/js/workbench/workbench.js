$(document).ready(function () {
    $(".menu-workbench").activeMenu(true);
});

$(document).ready(function () {
    $(".date-range").createDateRangePicker();
});

$(document).ready(function () {
    let data = [], labels = [], colors = [];

    $.each($("#project_status_chart").data("project-status-statistics"), function (key, value) {
        data.push(value);
        switch (key) {
            case "WAIT":
                labels.push("\u672a\u5f00\u59cb");
                colors.push("#c23321");
                break;
            case "PROGRESS":
                labels.push("\u8fdb\u884c\u4e2d");
                colors.push("#f39c12");
                break;
            case "FINISH":
                labels.push("\u7ed3\u675f");
                colors.push("#00a65a");
                break;
        }
    });

    new Chart($("#project_status_chart")[0].getContext("2d"), {
        type: "doughnut",
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
    let data = [], labels = [], colors = [];

    $.each($("#project_category_chart").data("project-category-statistics"), function (key, value) {
        data.push(value);
        labels.push(key);
        colors.push($.getRandomColor());
    });

    new Chart($("#project_category_chart")[0].getContext("2d"), {
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
        header: $.calendarHeader,
        buttonText: $.calendarButtonLanguage,
        editable: false,
        droppable: false
    });
});

$(document).ready(function () {
    $("#data_record_chart_search").find("input.create-time").data("daterangepicker").setStartDate(new Date(new Date().getTime() - 1000 * 60 * 60 * 24 * 30));
});
