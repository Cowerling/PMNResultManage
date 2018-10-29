$(document).ready(function () {
    $(".menu-map").activeMenu(true);
});

$(document).ready(function () {
    let base_image_layer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: CONSTANT.GEOSOURCE_TIANDITU_IMG
        }),
        visible: false
    }), base_vector_layer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: CONSTANT.GEOSOURCE_TIANDITU_VEC
        }),
        visible: false
    });

    let map = new ol.Map({
        target: "map",
        layers: [
            base_image_layer,
            base_vector_layer,
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: CONSTANT.GEOSOURCE_TIANDITU_CVA
                })
            })
        ],
        view: new ol.View({
            zoom: 10,
            center: CONSTANT.INIT_CENTER
        }),
        controls: ol.control.defaults().extend([
            new ol.control.ZoomSlider(),
            new ol.control.ScaleLine({
                units: "metric"
            })]),
        overlays: [
            new ol.Overlay({
                id: "feature_popup",
                element: $("#map").find("div.map-popup")[0],
                autoPan: true,
                autoPanAnimation: {
                    duration: 250
                }
            })
        ]
    });

    $("#map").find("div.map-popup").find("a.map-popup-closer").click(function (event) {
        map.getOverlayById("feature_popup").setPosition(undefined);
        this.blur();
        return false;
    });

    map.on("singleclick", function(event) {
        let feature_popup = this.getOverlayById("feature_popup");

        this.forEachFeatureAtPixel(event.pixel, function (feature, layer) {
             if (!!(feature) && feature_popup.getPosition() == undefined) {
                let content = $("#map").find("div.map-popup").find("div.map-popup-content");

                content.empty();
                content.append("<div class='callout callout-info' style='margin: 0px 0px 5px 0px; padding: 1px 1px 0px 1px;'><span>\u8be6\u7ec6\u4fe1\u606f</span></div>");
                $.each(feature.getProperties(), function (key, value) {
                    if (value instanceof ol.geom.Geometry) {
                        let coordinates = ol.proj.transform(value.getCoordinates(), "EPSG:" + CONSTANT.MAP_EPSG, feature.get("crs"));
                        content.append("<p><span class='lable label-status-min label-success'>\u6a2a\u5750\u6807</span><code>" + coordinates[0] + "</code><span class='lable label-status-min label-success'>\u7eb5\u5750\u6807</span><code>" + coordinates[1] + "</code></p>");
                    } else {
                        content.append("<p><span class='lable label-status-min label-warning'>" + key + "</span><code>" + value + "</code></p>");
                    }
                });

                feature_popup.setPosition(event.coordinate);
            }
        });
    });

    map.on("pointermove", function (event) {
        let hit = this.hasFeatureAtPixel(this.getEventPixel(event.originalEvent));
        this.getTargetElement().style.cursor = hit ? "pointer": "";
    });

    map.updateSize();

    function findLayer(map, layer_name) {
        let layers = map.getLayers();

        for (let i = 0, length = layers.getLength(); i < length; i++) {
            let layer = layers.item(i);
            if (layer.get("name") == layer_name) {
                return layer;
            }
        }
    }

    $.get("geoservice/server", function (server) {
        $("#map_tool").find("div.base-layer-list").bindLayer(new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: server.url + "/wms",
                params: {
                    format: "image/png",
                    version: "1.3.0",
                    authkey: server.authkey,
                    tiled: true,
                    layers: server.worksapce + ":hydro_polygon"
                }
            }),
            visible: false,
            name: "\u6c34\u7cfb",
            geometry_type: "polygon"
        }), function (layer) {
            map.addLayer(layer);
        });

        $("#map_tool").find("div.base-layer-list").bindLayer(new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: server.url + "/wms",
                params: {
                    format: "image/png",
                    version: "1.3.0",
                    authkey: server.authkey,
                    tiled: true,
                    layers: server.worksapce + ":highway_polyline"
                }
            }),
            visible: false,
            name: "\u516c\u8def",
            geometry_type: "polyline"
        }), function (layer) {
            map.addLayer(layer);
        });

        $("#map_tool").find("div.base-layer-list").bindLayer(new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: server.url + "/wms",
                params: {
                    format: "image/png",
                    version: "1.3.0",
                    authkey: server.authkey,
                    tiled: true,
                    layers: server.worksapce + ":railway_polyline"
                }
            }),
            visible: false,
            name: "\u94c1\u8def",
            geometry_type: "polyline"
        }), function (layer) {
            map.addLayer(layer);
        });

        $("#map_tool").find("div.base-layer-list").bindLayer(new ol.layer.Tile({
            source: new ol.source.TileWMS({
                url: server.url + "/wms",
                params: {
                    format: "image/png",
                    version: "1.3.0",
                    authkey: server.authkey,
                    tiled: true,
                    layers: server.worksapce + ":city_point"
                }
            }),
            visible: false,
            name: "\u57ce\u5e02",
            geometry_type: "point"
        }), function (layer) {
            map.addLayer(layer);
        });

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

                        let crs = result.crs.properties.name, epsg = crs.substr(crs.indexOf("EPSG::") + "EPSG::".length, 4);
                        let geometry = result.features[0].geometry, coordinates = geometry.coordinates[0];

                        let view = map.getView();
                        view.setCenter(ol.proj.transform(coordinates, "EPSG:" + epsg, "EPSG:" + CONSTANT.MAP_EPSG));
                        map.render();
                    });
            }
        });
    });

    $("#map_tool")
        .find("button.base-map-source")
        .text("\u5f71\u50cf").addClass("minimap-vector")
        .click(function () {
            let $this = $(this);

            $this.toggleClass("minimap-image minimap-vector");

            if ($this.hasClass("minimap-image")) {
                $this.text("\u5f71\u50cf");

                base_vector_layer.setVisible(false);
                base_vector_layer.setVisible(true);
            } else if ($this.hasClass("minimap-vector")) {
                $this.text("\u5730\u56fe");

                base_vector_layer.setVisible(false);
                base_image_layer.setVisible(true);
            }
        });

    $("#map_tool").find("button.base-map-source").click();

    let data_record_tag = $.UrlUtils.getParament("dataRecordTag"), data_record_name = $.UrlUtils.getParament("dataRecordName");
    if (data_record_tag != null && data_record_name != null) {
        $("#map_tool").find("div.custom-layer-list").bindLayer(new ol.layer.Vector({
            source: new ol.source.Vector({
                url: "geodata/wfs/" + data_record_tag,
                format: new ol.format.GeoJSON()
            }),
            style: function (feature, resolution) {
                return [
                    new ol.style.Style({
                        image: new ol.style.Icon({
                            src: "resources/image/pin.png"
                        }),
                        text: new ol.style.Text({
                            textAlign: "left",
                            textBaseline: "bottom",
                            font: "normal 20px KaiTi",
                            text: feature.getId(),
                            fill: new ol.style.Fill({
                                color: "#aa3300"
                            }),
                            stroke: new ol.style.Stroke({
                                color: "#ffcc33",
                                width: 2
                            })
                        })
                    })
                ];
            },
            name: data_record_name,
            geometry_type: "point"
        }), function (layer) {
            map.addLayer(layer);
        }, function (layer) {
            map.removeLayer(layer);
        });
    }
});

$(document).ready(function () {

});
