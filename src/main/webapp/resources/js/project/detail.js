$(document).ready(function () {
    $(".member-list").slimScroll({
        height: "250px"
    });
});

$(document).ready(function () {
    $(".message-remove").click(function (event) {
        event.preventDefault();
        $(this).parent().hide();
    });
});

$(document).ready(function () {
    $("#data_list_table").find(".search-condition").val(JSON.stringify({
        projectTag: [$("#data_list_table").find(".search-condition").val()],
        projectSingle: true
    }));
    $("#data_list_table").createDataListTable("../data/record/list", false);
});
