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

    let filter_draw_source = new ol.source.Vector();

    let map = new ol.Map({
        target: "map",
        layers: [
            base_image_layer,
            base_vector_layer,
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: CONSTANT.GEOSOURCE_TIANDITU_CVA
                })
            }),
            new ol.layer.Vector({
                source: filter_draw_source
            })
        ],
        view: new ol.View({
            zoom: 10,
            center: CONSTANT.INIT_CENTER
        }),
        controls: ol.control.defaults({
            attribution: false
        }).extend([
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
        if ($("#map_tool").find("button.filter").hasClass("active")) {
            return;
        }

        let feature_popup = this.getOverlayById("feature_popup");

        this.forEachFeatureAtPixel(event.pixel, function (feature, layer) {
             if (!!(feature) && feature_popup.getPosition() == undefined) {
                let content = $("#map").find("div.map-popup").find("div.map-popup-content");

                content.empty();
                content.append("<div class='callout callout-info' style='margin: 0px 0px 5px 0px; padding: 1px 1px 0px 1px;'><span>\u8be6\u7ec6\u4fe1\u606f</span></div>");
                $.each(feature.getProperties(), function (key, value) {
                    if (!(value instanceof ol.geom.Geometry)) {
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

    $.get(CONSTANT.GEOSERVICE_SERVER, function (server) {
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

    function addCustomLayer(data_record_tag, data_record_name) {
        let color = $.getRandomColor();

        let layer = new ol.layer.Vector({
            source: new ol.source.Vector({
                url: CONSTANT.GEOTATA_WFS + data_record_tag,
                format: new ol.format.GeoJSON()
            }),
            style: function (feature, resolution) {
                return $.createCustomLayerStyle("\u25c8" + feature.get("name"), color, "#ffff00");
            },
            name: data_record_name,
            geometry_type: "point",
            filterable: true
        });

        $("#map_tool").find("div.custom-layer-list").bindLayer(layer,
            function (layer) {
            map.addLayer(layer);
        }, function (layer) {
            map.getView().fit(layer.getSource().getExtent(), map.getSize());
        }, function (layer) {
            map.removeLayer(layer);
        });

        return layer;
    }

    let data_record_tag = $.UrlUtils.getParament("dataRecordTag"), data_record_name = $.UrlUtils.getParament("dataRecordName");
    if (data_record_tag != null && data_record_name != null) {
        addCustomLayer(data_record_tag, data_record_name).on("change", function (event) {
            map.getView().fit(this.getSource().getExtent(), map.getSize());
        });

        map.updateSize();
    } else {
        let local_city = new T.LocalCity();

        local_city.location(function (result) {
            let view = map.getView();
            view.setCenter(ol.proj.transform([result.lnglat.lng, result.lnglat.lat], "EPSG:" + CONSTANT.WGS84_EPSG, "EPSG:" + CONSTANT.MAP_EPSG));
            view.setZoom(result.level);
            map.render();
        });

        map.updateSize();
    }

    let $add_custom_layer_modal = $("#add_custom_layer_modal");

    $add_custom_layer_modal.find("button.ok").click(function (event) {
        $add_custom_layer_modal.getSelectedDataRecordTags().forEach((dataRecord) => {
            addCustomLayer(dataRecord.tag, dataRecord.name);
        });
    });

    $("#map_tool").find("button.full-extent").click(function (event) {
        map.getView().fit(CONSTANT.FULL_EXTENT, map.getSize());
    });

    let filter_draw = new ol.interaction.Draw({
        source: filter_draw_source,
        type: "Polygon",
        style: new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(30, 144, 255, 0.2)"
            }),
            stroke: new ol.style.Stroke({
                color: "#ffcc33",
                width: 2
            }),
            image: new ol.style.Circle({
                radius: 7,
                fill: new ol.style.Fill({
                    color: "#ffcc33"
                })
            })
        })
    });

    filter_draw.on("drawend",function(event) {
        let geometry = event.feature.getGeometry();

        if (!!(geometry) == false) {
            return;
        }

        let feature_popup =  map.getOverlayById("feature_popup");

        let content = $("#map").find("div.map-popup").find("div.map-popup-content");
        content.empty();
        content.append("<div class='callout callout-danger' style='margin: 0px 0px 5px 0px; padding: 1px 1px 0px 1px;'><span>\u7b5b\u9009\u7ed3\u679c</span></div>");

        let count = 0;
        map.getLayers().forEach(function (layer) {
            if (layer.get("filterable") == true) {
                let features = layer.getSource().getFeaturesInExtent(geometry.getExtent());
                for (let i = 0, length = features.length; i < length; i++) {
                    let feature = features[i];
                    if (geometry.intersectsCoordinate(feature.getGeometry().getCoordinates())) {
                        content.append("<span class='lable label-status-min label-success' style='margin-right: 5px;'>" + ++count + "</span>");
                        $.each(feature.getProperties(), function (key, value) {
                            if (!(value instanceof ol.geom.Geometry)) {
                                content.append("<span class='lable label-status-min label-warning'>" + key + "</span><code style='margin-right: 3px;'>" + value + "</code>");
                            }
                        });
                        content.append("<br />");
                        content.append("<br />");
                    }
                }
            }
        });

        event.feature.set("\u7b5b\u9009\u8303\u56f4" + geometry.getExtent(), content.html());

        feature_popup.setPosition(ol.extent.getCenter(geometry.getExtent()));
    });

    $("#map_tool").find("button.filter").click(function (event) {
        let button = $(this);
        button.toggleClass("text-yellow active");

        if (button.hasClass("active")) {
            map.addInteraction(filter_draw);
        } else {
            map.removeInteraction(filter_draw);
        }
    });
});

$(document).ready(function () {
    let $add_custom_layer_modal = $("#add_custom_layer_modal");

    $add_custom_layer_modal.on("show.bs.modal", function (event) {
        $add_custom_layer_modal.find("div.tree-view").treeview({
            levels: 1,
            showTags: true,
            searchResultBackColor: "#ffe500",
            expandIcon: "glyphicon glyphicon-chevron-right",
            collapseIcon: "glyphicon glyphicon-chevron-down",
            multiSelect: true,
            data: (function () {
                let projects = []

                $.ajax({
                    url: CONSTANT.DATA_RECORD_LIST_SUMMARY_URL,
                    data: {
                        search: JSON.stringify({
                            spatialOnly: true
                        })
                    },
                    async: false,
                    success: function(result){
                        result.forEach((project) => {
                            let dataRecords = [];

                            project.dataRecords.forEach((dataRecord) => {
                                if ($.inArray("VIEW", dataRecord.authorities) >= 0) {
                                    $.extend(dataRecord, {
                                        text: dataRecord.name,
                                        selectable: true,
                                        icon: "glyphicon glyphicon-tags"
                                    });

                                    dataRecords.push(dataRecord);
                                }
                            });

                            $.extend(project, {
                                text: project.name,
                                tags: [project.dataRecords.length],
                                selectable: false,
                                icon: "glyphicon glyphicon-folder-open",
                                nodes: dataRecords
                            });

                            projects.push(project);
                        });
                    }
                });

                return projects;
            })()
        });
    });

    $add_custom_layer_modal.find("button.search").click(function (event) {
        $add_custom_layer_modal.find(".tree-view").treeview("search", [$add_custom_layer_modal.find("input.search-content").val(), {
            exactMatch: false,
            revealResults: true
        }]);
    });
});
