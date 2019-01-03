package com.cowerling.pmn.web.spatial;

import com.cowerling.pmn.data.DataRepository;
import com.cowerling.pmn.domain.data.DataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.domain.data.DataRecordStatus;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.ResourceNotFoundException;
import com.cowerling.pmn.security.GeneralEncoderService;
import com.cowerling.pmn.utils.DataUtils;
import com.cowerling.pmn.utils.DateUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.GeometryBuilder;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/geodata")
@SessionAttributes({"loginUser"})
public class GeoDataController {
    private static final String FEATURE_TYPE_GEOMETRY_NAME = "geometry";

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${file.data.location}")
    private String dataFileLocation;

    @RequestMapping(value = "/wfs/{dataRecordTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody String wfs(@PathVariable("dataRecordTag") String dataRecordTag,
                                     @ModelAttribute("loginUser") final User loginUser) throws Exception {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (!dataRecord.getCategory().isSpatial()) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.VIEW)) {
                throw new RuntimeException();
            }

            if (dataRecord.getStatus() == DataRecordStatus.QUALIFIED) {
                return dataRepository.findDataContentsAsGeoJsonByDataRecord(dataRecord);
            } else {
                List<? extends DataContent> dataContents = DataUtils.getDataFileContents(dataRecord, dataFileLocation);

                if (dataContents.size() == 0) {
                    throw new RuntimeException();
                }

                DefaultFeatureCollection defaultFeatureCollection = null;
                SimpleFeatureType simpleFeatureType = null;
                GeometryBuilder geometryBuilder = new GeometryBuilder();

                for (DataContent dataContent : dataContents) {
                    if (dataContent.getGeometry() == null) {
                        continue;
                    }

                    Map<String, Map.Entry<Class<?>, Object>> normalAttributes = dataContent.attributes();

                    if (defaultFeatureCollection == null) {
                        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
                        simpleFeatureTypeBuilder.setName(dataRecord.getName());
                        simpleFeatureTypeBuilder.add(FEATURE_TYPE_GEOMETRY_NAME, Point.class);
                        for (Map.Entry<String, Map.Entry<Class<?>, Object>> entry : normalAttributes.entrySet()) {
                            simpleFeatureTypeBuilder.add(entry.getKey(), entry.getValue().getKey());
                        }
                        simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();
                        defaultFeatureCollection = new DefaultFeatureCollection(null, simpleFeatureType);
                    }

                    List<Object> values = new ArrayList<>() {
                        {
                            org.postgis.Point point = dataRepository.findDataContentTransformPointByDataRecord(dataRecord, dataContent);
                            add(geometryBuilder.point(point.x, point.y));
                            addAll(normalAttributes.values().stream().map(value -> value.getValue() instanceof Date ? DateUtils.format((Date) value.getValue()) : value.getValue()).collect(Collectors.toList()));
                        }
                    };

                    defaultFeatureCollection.add(SimpleFeatureBuilder.build(simpleFeatureType, values, dataContent.getName()));
                }


                StringWriter featureCollectionStringWriter = new StringWriter();
                FeatureJSON featureJSON = new FeatureJSON();

                featureJSON.writeFeatureCollection(defaultFeatureCollection, featureCollectionStringWriter);

                return featureCollectionStringWriter.toString();
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
