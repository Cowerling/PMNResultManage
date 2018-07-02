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

});
