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
import com.cowerling.pmn.web.ConstantValue;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.referencing.CRS;
import org.json.JSONObject;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/geodata")
@SessionAttributes({"loginUser"})
public class GeoDataController {
    private static final String FEATURE_TYPE_GEOMETRY_NAME = "geometry";
    private static final String FEATURE_TYPE_H_NAME = "h";
    private static final String FEATURE_TYPE_CRS_NAME = "crs";

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GeneralEncoderService generalEncoderService;

    @Value("${file.data.location}")
    private String dataFileLocation;

    @RequestMapping(value = "/wfs/{dataRecordTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String wfs(@PathVariable("dataRecordTag") String dataRecordTag,
                                     @ModelAttribute("loginUser") final User loginUser) throws Exception {
        try {
            DataRecord dataRecord = dataRepository.findDataRecordsById(Long.parseLong(generalEncoderService.staticDecrypt(dataRecordTag)));

            if (dataRecord == null) {
                throw new RuntimeException();
            }

            List<DataRecordAuthority> authorities = dataRepository.findDataRecordAuthorities(dataRecord, loginUser);

            if (!authorities.contains(DataRecordAuthority.VIEW)) {
                throw new RuntimeException();
            }

            List<? extends DataContent> dataContents = dataRecord.getStatus() == DataRecordStatus.QUALIFIED ?
                    dataRepository.findDataContentsByDataRecord(dataRecord) :
                    DataUtils.getDataFileContents(dataRecord, dataFileLocation);

            if (dataContents == null || dataContents.size() == 0) {
                throw new RuntimeException();
            }

            DefaultFeatureCollection defaultFeatureCollection = null;
            SimpleFeatureType simpleFeatureType = null;
            GeometryBuilder geometryBuilder = new GeometryBuilder();
            int id = 0;
            String crs = null;

            for (DataContent dataContent : dataContents) {
                Map<String, Map.Entry<Class<?>, Object>> normalAttributes = dataContent.normalAttributes();

                if (defaultFeatureCollection == null) {
                    SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
                    simpleFeatureTypeBuilder.setName(dataRecord.getName());
                    simpleFeatureTypeBuilder.add(FEATURE_TYPE_GEOMETRY_NAME, Point.class);
                    simpleFeatureTypeBuilder.add(FEATURE_TYPE_H_NAME, Double.class);
                    for (Map.Entry<String, Map.Entry<Class<?>, Object>> entry : normalAttributes.entrySet()) {
                        simpleFeatureTypeBuilder.add(entry.getKey(), entry.getValue().getKey());
                    }
                    simpleFeatureTypeBuilder.add(FEATURE_TYPE_CRS_NAME, String.class);
                    simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();
                    defaultFeatureCollection = new DefaultFeatureCollection(null, simpleFeatureType);
                }

                if (crs == null) {
                    crs = dataContent.getCrs();
                }

                List<Object> values = new ArrayList<>() {
                    {
                        add(geometryBuilder.point(dataContent.getCoordinateX(), dataContent.getCoordinateY()));
                        add(dataContent.getCoordinateH());
                        addAll(normalAttributes.values().stream().map(value -> value.getValue()).collect(Collectors.toList()));
                        add(dataContent.getCrs());
                    }
                };

                defaultFeatureCollection.add(SimpleFeatureBuilder.build(simpleFeatureType, values, dataRecord.getName() + "-" + (++id)));
            }


            StringWriter featureCollectionStringWriter = new StringWriter(), crsStringWriter = new StringWriter();
            FeatureJSON featureJSON = new FeatureJSON();

            featureJSON.writeFeatureCollection(defaultFeatureCollection, featureCollectionStringWriter);
            featureJSON.writeCRS(CRS.decode(crs), crsStringWriter);

            JSONObject jsonObject = new JSONObject(featureCollectionStringWriter.toString());
            jsonObject.put("crs", new JSONObject(crsStringWriter.toString()));

            return jsonObject.toString();
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
    }
}
