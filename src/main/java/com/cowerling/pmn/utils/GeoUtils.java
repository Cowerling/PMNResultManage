package com.cowerling.pmn.utils;

import java.util.*;

public class GeoUtils {
    private static final String WGS_84 = "WGS84";
    private static final String CGCS_2000 = "CGCS2000";
    private static final String XIAN_80 = "XIAN80";
    private static final String BEIJING_54 = "BEIJING54";

    private static final String WGS_84_PROJ = "+proj=geocent +datum=WGS84 +units=m +no_defs";
    private static final String CGCS_2000_PROJ = "+proj=geocent +ellps=GRS80 +units=m +no_defs";

    public static final Integer WGS_84_EPSG = 4326;

    public enum GeoDefine {
        PROJ, H, ORIGIN
    }

    public static Map<GeoDefine, String> getGeoDefines(String define) {
        String proJ = null, h = null;

        String[] subDefines = define.split(" ");

        if (subDefines.length == 1) {
            String[] keyValue = subDefines[0].trim().split("=");

            if (keyValue.length == 2 && keyValue[0].toUpperCase().equals("CRS")) {
                switch (keyValue[1].toUpperCase()) {
                    case WGS_84:
                        proJ = WGS_84_PROJ;
                        break;
                    case CGCS_2000:
                        proJ = CGCS_2000_PROJ;
                        break;
                    default:
                        break;
                }
            }
        } else {
            List<String> subProjs = new ArrayList<>();

            for (String subDefine : subDefines) {
                String[] keyValue = subDefine.trim().split("=");

                if (keyValue.length != 2) {
                    continue;
                }

                if (keyValue[0].toUpperCase().equals("CRS")) {
                    switch (keyValue[1].toUpperCase()) {
                        case CGCS_2000:
                            subProjs.add("+ellps=GRS80");
                            break;
                        case XIAN_80:
                            subProjs.add("+a=6378140 +b=6356755.288157528");
                            break;
                        case BEIJING_54:
                            subProjs.add("+ellps=krass +towgs84=15.8,-154.4,-82.3,0,0,0,0");
                            break;
                        default:
                            break;
                    }
                } else if (keyValue[0].toUpperCase().equals("LON")) {
                    subProjs.add("+lon_0=" + keyValue[1]);
                } else if (keyValue[0].toUpperCase().equals("H")) {
                    h = keyValue[1];
                }
            }

            proJ = "+proj=tmerc +lat_0=0 +k=1 +x_0=500000 +y_0=0 +units=m +no_defs " + String.join( " ", subProjs);
        }

        final String PROJ = proJ, H = h;

        return new HashMap<>() {
            {
                put(GeoDefine.PROJ, PROJ);
                put(GeoDefine.H, H);
                put(GeoDefine.ORIGIN, define);
            }
        };
    }
}
