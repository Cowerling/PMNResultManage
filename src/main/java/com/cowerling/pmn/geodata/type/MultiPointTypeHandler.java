package com.cowerling.pmn.geodata.type;

import org.apache.ibatis.type.MappedTypes;
import org.postgis.MultiPoint;

@MappedTypes(MultiPoint.class)
public class MultiPointTypeHandler extends AbstractGeometryTypeHandler<MultiPoint> {
}
