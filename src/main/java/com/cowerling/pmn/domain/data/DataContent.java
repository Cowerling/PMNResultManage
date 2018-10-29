package com.cowerling.pmn.domain.data;

import com.cowerling.pmn.annotation.CoordinateH;
import com.cowerling.pmn.annotation.CoordinateX;
import com.cowerling.pmn.annotation.CoordinateY;
import com.cowerling.pmn.utils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DataContent {
    public static final String EPSG_WGS84 = "EPSG:4326";

    protected Long id;
    protected String crs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrs() {
        return crs;
    }

    public Double getCoordinateX() {
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

    public Double getCoordinateH() {
        try {
            return ClassUtils.getDeclaredFieldValue(this, CoordinateH.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public Map<String, Map.Entry<Class<?>, Object>> normalAttributes() {
        try {
            Map<String, Map.Entry<Class<?>, Object>> attributes = new HashMap<>();

            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(CoordinateX.class) ||
                        field.isAnnotationPresent(CoordinateY.class) ||
                        field.isAnnotationPresent(CoordinateH.class)) {
                    continue;
                }

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
        DataContent dataContent = this;

        return new ArrayList<>() {
            {
                add(ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateX.class).getName());
                add(ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateY.class).getName());
                add(ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateH.class).getName());
                addAll(dataContent.normalAttributes().keySet());
            }
        };
    }

    public List<Object> values() {
        try {
            DataContent dataContent = this;

            return new ArrayList<>() {
                {
                    add(ClassUtils.getDeclaredFieldValue(dataContent, ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateX.class)));
                    add(ClassUtils.getDeclaredFieldValue(dataContent, ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateY.class)));
                    add(ClassUtils.getDeclaredFieldValue(dataContent, ClassUtils.getDeclaredField(dataContent.getClass(), CoordinateH.class)));
                    addAll(dataContent.normalAttributes().values().stream().map(value -> value.getValue()).collect(Collectors.toList()));
                }
            };
        } catch (InvocationTargetException e) {
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
}
