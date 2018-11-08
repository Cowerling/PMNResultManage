package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.FieldOrder;
import com.cowerling.pmn.utils.ClassUtils;
import org.postgis.Geometry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DataContent {
    public static final String EPSG_WGS84 = "EPSG:4326";

    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract String getName();

    public abstract Geometry getGeometry();

    public abstract String getTableName();

    /*public Double getCoordinateX() {
        try {
            return ClassUtils.getDeclaredFieldValue(this, CoordinateX.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public Double getCoordinateY() {
        try {
            return ClassUtils.getDeclaredFieldValue(this, CoordinateY.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public Double getCoordinateZ() {
        try {
            return ClassUtils.getDeclaredFieldValue(this, CoordinateZ.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }*/

    public Map<String, Map.Entry<Class<?>, Object>> attributes() {
        try {
            Map<String, Map.Entry<Class<?>, Object>> attributes = new LinkedHashMap<>();

            List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());
            fields.sort(Comparator.comparingInt(item -> item.getAnnotation(FieldOrder.class).order()));

            for (Field field : fields) {
                attributes.put(field.getName(), new AbstractMap.SimpleEntry(field.getType(), ClassUtils.getDeclaredFieldValue(this, field)));
            }

            return attributes;
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public List<String> attributeNames() {

        return this.attributes().keySet().stream().collect(Collectors.toList());
    }

    public List<Object> values() {
        return this.attributes().values().stream().map(value -> value.getValue()).collect(Collectors.toList());
    }
}
