var CONSTANT = {}

$(document).ready(function () {
    Object.defineProperty(CONSTANT, "LOCATION_SERVER_URL", {
        value: "http://whois.pconline.com.cn/ipJson.jsp",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "GEOSOURCE_TIANDITU_VEC", {
        value: "http://t3.tianditu.com/DataServer?T=vec_w&x={x}&y={y}&l={z}",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "GEOSOURCE_TIANDITU_IMG", {
        value: "http://t3.tianditu.com/DataServer?T=img_w&x={x}&y={y}&l={z}",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "GEOSOURCE_TIANDITU_CVA", {
        value: "http://t3.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "MAP_EPSG", {
        value: 3857,
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "WGS84_EPSG", {
        value: 4326,
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "INIT_CENTER", {
        value: [12955467.3459742, 4854848.3478852],
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "FULL_EXTENT", {
        value: [8199793.691832532, 1817464.898135734, 15033697.231631598, 7085388.165495084],
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "GEOTATA_WFS", {
        value: "geodata/wfs/",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "GEOSERVICE_SERVER", {
        value: "geoservice/server",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "PROJECT_REMOVE_URL", {
        value: "remove",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DEPARTMENT_LIST_URL", {
        value: "../department/list",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DEPARTMENT_USERS_URL", {
        value: "../department/users",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DATA_TEMPLET_FILE_URL", {
        value: "../resources/data/templet/",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DATA_TEMPLET_FILE_URL", {
        value: "../resources/data/templet/",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DEPARTMENT_PROJECT_USERS_URL", {
        value: "../department/projectUsers",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DATA_RECORD_LIST_URL", {
        value: "../data/record/list",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DATA_RECORD_LIST_SUMMARY_URL", {
        value: "data/record/list/summary",
        enumerable: true
    });

    Object.defineProperty(CONSTANT, "DATA_AUTHORITY_URL", {
        value: "authority/",
        enumerable: true
    });
});
