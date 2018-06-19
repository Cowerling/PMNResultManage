$(document).ready(function () {
    var interval = 1;
    var total_time = interval * 5;

    $("#count_down").text(total_time);

    setInterval(function () {
        $("#count_down").text((total_time -= interval));

        if (total_time == 0) {
            $("#sign_in")[0].click();
        }
    }, interval * 1000);
});