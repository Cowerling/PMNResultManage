$(document).ready(function () {
    $(".menu-data").activeMenu();
    $(".menu-data-list").activeMenu();
});

$(document).ready(function () {
    $("#data_list_table").createDataListTable("./record/list", true);
});