$(document).ready(function () {
    let $data_spreadsheet = $("#data_spreadsheet");
    let last_id = $data_spreadsheet.data("ids")[$data_spreadsheet.data("ids").length - 1];

    $("#data_insert").click(function (event) {
        $data_spreadsheet.jexcel("insertRow", 1);
        $data_spreadsheet.data("ids").push(++last_id);
        $data_spreadsheet.data("change-content").insert.push(last_id);
        $data_spreadsheet.data("selection").id = last_id;
    });

    $("#data_delete").click(function (event) {
        if ($data_spreadsheet.data("selection") != "" && $data_spreadsheet.data("selection") != null) {
            let row = $data_spreadsheet.data("selection").row, id = $data_spreadsheet.data("selection").id;
            $data_spreadsheet.jexcel("deleteRow", row);

            $data_spreadsheet.data("ids").splice($.inArray(id, $data_spreadsheet.data("ids")), 1);

            let insertIndex = $.inArray(id, $data_spreadsheet.data("change-content").insert),
                updateIndex = $.inArray(id, $data_spreadsheet.data("change-content").update);

            if (insertIndex >= 0) {
                $data_spreadsheet.data("change-content").insert.splice(insertIndex, 1);
            } else {
                $data_spreadsheet.data("change-content").delete.push(id);
            }

            if (updateIndex >= 0) {
                $data_spreadsheet.data("change-content").update.splice(updateIndex, 1);
            }

            $data_spreadsheet.data("selection", null);
        }
    });

    $("#data_save").click(function (event) {
        let change_content = {
            updates: [],
            inserts: [],
            deletes: $data_spreadsheet.data("change-content").delete
        };

        $.each($data_spreadsheet.data("change-content").update, function (index, id) {
            let update = [id];
            for (let i = 0, length = $data_spreadsheet.data("headers").length; i < length; i++) {
                update.push($data_spreadsheet.jexcel("getValue", String.fromCharCode(i + 65) + ($.inArray(id, $data_spreadsheet.data("ids")) + 1)));
            }

            change_content.updates.push(update);
        });

        $.each($data_spreadsheet.data("change-content").insert, function (index, id) {
            let insert = [];
            for (let i = 0, length = $data_spreadsheet.data("headers").length; i < length; i++) {
                insert.push($data_spreadsheet.jexcel("getValue", String.fromCharCode(i + 65) + ($.inArray(id, $data_spreadsheet.data("ids")) + 1)));
            }

            change_content.inserts.push(insert);
        });

        $.post(window.location.href.replace("view", "edit"), {
            _csrf: $("form.data-save").find("input[name='_csrf']").val(),
            changeContent: JSON.stringify(change_content)
        }, function (result) {
            $("#edit_result_modal").find(".result-success").show();
            $("#edit_result_modal").find(".result-failed").hide();
            $("#edit_result_modal").modal("show");
        }).fail (function (result) {
            $("#edit_result_modal").find(".result-failed").show();
            $("#edit_result_modal").find(".result-success").hide();
            $("#edit_result_modal").modal("show");
        })
    });
});