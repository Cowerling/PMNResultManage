$(document).ready(function () {
    let $data_spreadsheet = $("#data_spreadsheet");

    let index_column_width = 30, column_widths = [];
    for (let i = 0, length = $data_spreadsheet.data("headers").length; i < length; i++) {
        column_widths.push(($data_spreadsheet.width() - index_column_width) / length);
    }

    function getPosition(spreadsheet, id) {
        let column_row = id.split("-");
        return {
            row: parseInt(column_row[1]),
            id: spreadsheet.data("ids")[parseInt(column_row[1])]
        };
    }

    $data_spreadsheet.jexcel({
        data: $data_spreadsheet.data("values"),
        colHeaders: $data_spreadsheet.data("headers"),
        colWidths: column_widths,
        contextMenu: function() {
            return null;
        },
        editable: $data_spreadsheet.data("editable"),
        rowDrag: false,
        columnSorting: false,
        onload: function (event) {
            $data_spreadsheet.find("td[title]").addClass("label-success");
            $data_spreadsheet.data("change-content", {
                update: [],
                insert: [],
                delete: []
            });
        },
        onchange: function (obj, cell, value) {
            let position = getPosition($data_spreadsheet, $(cell).prop("id"));
            if ($.inArray(position.id, $data_spreadsheet.data("change-content").insert) < 0 &&
                $.inArray(position.id, $data_spreadsheet.data("change-content").update) < 0) {
                $data_spreadsheet.data("change-content").update.push(position.id);
            }
        },
        onselection: function (obj, cell, value) {
            $data_spreadsheet.data("selection", getPosition($data_spreadsheet, $(cell).prop("id")));
        }
    });
});