package com.cowerling.pmn.geodata.type;

import org.apache.ibatis.type.MappedTypes;
import org.postgis.LineString;

@MappedTypes(LineString.class)
public class LineStringTypeHandler extends AbstractGeometryTypeHandler<LineString> {
}
