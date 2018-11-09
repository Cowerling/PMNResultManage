$(document).ready(function () {
    $(".select2").select2({
        language: "zh-CN"
    });
});

$(document).ready(function () {
    let $list_table = $("#list_table");

    const columns = [
        {
            name: "user_name",
            data: "user_name"
        },
        {
            name: "alias",
            data: "alias"
        },
        {
            name: "register_date",
            data: "register_date"
        },
        {
            name: "role",
            data: "role"
        },
        {
            name: "department",
            data: "department"
        },
        {
            name: "operation",
            data: "operation"
        }
    ];

    $list_table.on("draw.dt", function () {
        $(this).find("button.edit").click(function (event) {
            let $user_edit_modal = $("#user_edit_modal");

            $user_edit_modal.find("input.alias").val($(this).data("alias"));
            $user_edit_modal.find("input[name=userName]").val($(this).data("user-name"));
            $user_edit_modal.find("select[name=role]").val($(this).data("role")).trigger("change");
            $user_edit_modal.find("input[name=department]").val($(this).data("department"));

            $("#user_edit_modal").modal("show");
        });

        $(this).find("button.delete").click(function (event) {
            let $user_delete_modal = $("#user_delete_modal");

            $user_delete_modal.find("span.alias").text($(this).data("alias"));
            $user_delete_modal.find("input[name=userName]").val($(this).data("user-name"));

            $("#user_delete_modal").modal("show");
        });
    }).DataTable({
        columns: columns,
        searching: false,
        serverSide: true,
        processing: true,
        ajax: {
            url: "../user/list",
            data: function (parameters) {
                return $.extend({}, {
                    request: JSON.stringify(parameters)
                });
            },
            dataSrc: function (result) {
                result.recordsTotal = result.users.length;
                result.recordsFiltered = result.users.length;

                for (let i = 0, length = result.users.length; i < length; i++) {
                    result.users[i].user_name = result.users[i].name;
                    result.users[i].register_date = result.users[i].registerDate;
                    result.users[i].role = result.users[i].userRole;
                    result.users[i].department = result.users[i].department != null ? result.users[i].department.name : "";
                    result.users[i].operation = "<button class='btn btn-primary btn-xs edit' style='margin-right: 2px;' data-user-name='" + result.users[i].name + "' data-alias='" + result.users[i].alias + "' data-role='" + result.users[i].userRole + "' data-department='" + result.users[i].department + "'>\u4fee\u6539</button><button class='btn btn-danger btn-xs delete' data-user-name='" + result.users[i].name + "' data-alias='" + result.users[i].alias + "'>\u5220\u9664</button>";
                }

                return result.users;
            }
        },
        language: $.listTableLanguage
    });
});