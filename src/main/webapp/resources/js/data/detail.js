$(document).ready(function () {
    var $data_spreadsheet = $("#data_spreadsheet");

    var index_column_width = 30, column_widths = [];
    for (var i = 0, length = $data_spreadsheet.data("headers").length; i < length; i++) {
        column_widths.push(($data_spreadsheet.width() - index_column_width) / length);
    }

    $data_spreadsheet.jexcel({
        data: $data_spreadsheet.data("values"),
        colHeaders: $data_spreadsheet.data("headers"),
        colWidths: column_widths
    });
});