package com.cowerling.pmn.web.data;

import com.cowerling.pmn.annotation.ToResourceNotFound;
import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.EncoderServiceException;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DateUtils;
import com.cowerling.pmn.web.ConstantValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;
import static com.cowerling.pmn.data.provider.DataSqlProvider.Order.ASCENDING;
import static com.cowerling.pmn.data.provider.DataSqlProvider.Order.DESCENDING;
import static com.cowerling.pmn.data.provider.DataSqlProvider.RecordField.*;

@RestController
@RequestMapping("/data/record")
@SessionAttributes({"loginUser"})
public class DataRecordController {
    private static final String LIST_REQUEST_COLUMN_NAME = "name";
    private static final String LIST_REQUEST_COLUMN_PROJECT_NAME = "projectName";
    private static final String LIST_REQUEST_COLUMN_UPLOADER_NAME = "uploaderName";
    private static final String LIST_REQUEST_COLUMN_UPLOAD_TIME = "uploadTime";
    private static final String LIST_REQUEST_COLUMN_STATUS = "status";
    private static final String LIST_REQUEST_COLUMN_REMARK = "remark";
    private static final String LIST_SEARCH_NAME = "name";
    private static final String LIST_SEARCH_PROJECT_SINGLE = "projectSingle";
    private static final String LIST_SEARCH_PROJECT_TAG = "projectTag";
    private static final String LIST_SEARCH_UPLOADER_NAME = "uploaderName";
    private static final String LIST_SEARCH_UPLOAD_TIME = "uploadTime";
    private static final String LIST_SEARCH_STATUS = "status";
    private static final String LIST_SEARCH_REMARK = "remark";

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ToResourceNotFound
    public @ResponseBody Map<String, Object> list (
            @RequestParam(value = "request") String request,
            @RequestParam(value = "search") String search,
            @ModelAttribute("loginUser") final User loginUser) throws ResourceNotFoundException {
        try {
            JSONObject requestJsonObject = new JSONObject(request);
            int draw = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_DRAW), start = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_START), length = requestJsonObject.getInt(ConstantValue.LIST_REQUEST_LENGTH);
            JSONArray tableColumns = requestJsonObject.getJSONArray(ConstantValue.LIST_REQUEST_COLUMNS);
            JSONArray tableOrders = requestJsonObject.getJSONArray(ConstantValue.LIST_REQUEST_ORDER);

            List<Pair<RecordField, Order>> orders = new ArrayList<>();
            tableOrders.forEach(item -> {
                JSONObject tableOrder = (JSONObject) item;
                String columnName = tableColumns.getJSONObject(tableOrder.getInt(ConstantValue.LIST_REQUEST_ORDER_COLUMN)).getString(ConstantValue.LIST_REQUEST_COLUMNS_NAME);
                Order order = tableOrder.getString(ConstantValue.LIST_REQUEST_ORDER_DIR).equals(ConstantValue.LIST_REQUEST_ORDER_ASC) ? ASCENDING : DESCENDING;

                switch (columnName) {
                    case LIST_REQUEST_COLUMN_NAME:
                        orders.add(Pair.of(NAME, order));
                        break;
                    case LIST_REQUEST_COLUMN_PROJECT_NAME:
                        orders.add(Pair.of(PROJECT_NAME, order));
                        break;
                    case LIST_REQUEST_COLUMN_UPLOADER_NAME:
                        orders.add(Pair.of(UPLOADER_NAME, order));
                        break;
                    case LIST_REQUEST_COLUMN_UPLOAD_TIME:
                        orders.add(Pair.of(UPLOAD_TIME, order));
                        break;
                    case LIST_REQUEST_COLUMN_STATUS:
                        orders.add(Pair.of(STATUS, order));
                        break;
                    case LIST_REQUEST_COLUMN_REMARK:
                        orders.add(Pair.of(REMARK, order));
                        break;
                    default:
                        break;
                }
            });

            Map<RecordField, Object> filters = new HashMap<>();
            JSONObject searchJsonObject = new JSONObject(search);

            if (searchJsonObject.has(LIST_SEARCH_NAME)) {
                JSONArray searchNames = searchJsonObject.getJSONArray(LIST_SEARCH_NAME);
                List<String> names = searchNames.toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(RecordField.NAME, names);
            }

            if (searchJsonObject.has(LIST_SEARCH_UPLOAD_TIME)) {
                JSONArray searchUploadTimes = searchJsonObject.getJSONArray(LIST_SEARCH_UPLOAD_TIME);
                Date startUploadTime = DateUtils.parse(searchUploadTimes.getString(0)), endUploadTime = DateUtils.parse(searchUploadTimes.getString(1));

                if (startUploadTime.before(endUploadTime)) {
                    filters.put(RecordField.START_UPLOAD_TIME, startUploadTime);
                    filters.put(RecordField.END_UPLOAD_TIME, endUploadTime);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_STATUS)) {
                JSONArray searchStatus = searchJsonObject.getJSONArray(LIST_SEARCH_STATUS);
                List<String> status = searchStatus.toList().stream().map(item -> item.toString()).collect(Collectors.toList());
                filters.put(RecordField.STATUS, status);
            }

            if (searchJsonObject.has(LIST_SEARCH_REMARK)) {
                String remark = searchJsonObject.getString(LIST_SEARCH_REMARK);
                if (StringUtils.isNotEmpty(remark)) {
                    filters.put(RecordField.REMARK, remark);
                }
            }

            if (searchJsonObject.has(LIST_SEARCH_PROJECT_TAG)) {
                List<Long> projects = searchJsonObject.getJSONArray(LIST_SEARCH_PROJECT_TAG).toList().stream().map(item -> {
                    try {
                        return Long.parseLong(generalEncoderService.staticDecrypt(item.toString()));
                    } catch (EncoderServiceException e) {
                        throw new RuntimeException();
                    }
                }).collect(Collectors.toList());

                filters.put(RecordField.PROJECT, projects);
            }

            if (searchJsonObject.has(LIST_SEARCH_UPLOADER_NAME)) {
                List<String> uploaders = searchJsonObject.getJSONArray(LIST_SEARCH_UPLOADER_NAME).toList().stream().map(Object::toString).collect(Collectors.toList());
                filters.put(RecordField.UPLOADER_NAME, uploaders);
            }

            List<DataRecord> dataRecords = dataRepository.findDataRecordsByUser(loginUser, filters, orders, start, length);
            dataRecords.forEach(dataRecord -> {
                try {
                    dataRecord.setAuthorities(dataRepository.findDataRecordAuthorities(dataRecord, loginUser));
                    dataRecord.setTag(generalEncoderService.staticEncrypt(dataRecord.getId()));
                } catch (EncoderServiceException e) {
                    throw new RuntimeException();
                }
            });

            Map<String, Object> list = new HashMap<>();

            if (searchJsonObject.has(LIST_SEARCH_PROJECT_SINGLE) && searchJsonObject.getBoolean(LIST_SEARCH_PROJECT_SINGLE)) {
                Long count = dataRepository.findDataRecordCountByUser(loginUser, ((List<Long>) filters.get(RecordField.PROJECT)).get(0));
                list.put("count", count);
            } else {
                Long count = dataRepository.findDataRecordCountByUser(loginUser);
                list.put("count", count);
            }

            list.put("draw", draw);
            list.put("dataRecords", dataRecords);

            return list;

        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
