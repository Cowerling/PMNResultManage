$(document).ready(function () {
    $(".menu-map").activeMenu(true);
});

$(document).ready(function () {
    var map = new ol.Map({
        target: "map",
        layers: [
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: CONSTANT.GEOSOURCE_TIANDITU_VEC
                })
            }),
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: CONSTANT.GEOSOURCE_TIANDITU_CVA
                })
            })
        ],
        view: new ol.View({
            zoom: 10,
            center: CONSTANT.INIT_CENTER
        })
    });

    map.updateSize();

    $.get("geoservice/server", function (server) {
        map.addLayer(new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: server.url + "/wms",
                params: {
                    format: "image/png",
                    version: "1.3.0",
                    authkey: server.authkey,
                    tiled: true,
                    layers: server.worksapce + ":city_point"
                }
            })
        }));

        $.ajax({
            dataType: "jsonp",
            url: CONSTANT.LOCATION_SERVER_URL,
            success: function(result){
                var city_name = result.city.slice(0, -1);

                $.get(server.url + "/wfs", {
                        service: "wfs",
                        version: "1.0.0",
                        request: "GetFeature",
                        typeName: server.worksapce + ":city_point",
                        maxFeatures: 1,
                        outputFormat: "application/json",
                        cql_filter: "name LIKE '%" + city_name + "%'",
                        authkey: server.authkey
                    },
                    function (result) {
                        if (result.totalFeatures == 0) {
                            return;
                        }

                        var crs = result.crs.properties.name, epsg = crs.substr(crs.indexOf("EPSG::") + "EPSG::".length, 4);
                        var geometry = result.features[0].geometry, coordinates = geometry.coordinates[0];

                        var view = map.getView();
                        view.setCenter(ol.proj.transform(coordinates, "EPSG:" + epsg, "EPSG:" + CONSTANT.MAP_EPSG));
                        map.render();
                    });
            }
        });
    });
});