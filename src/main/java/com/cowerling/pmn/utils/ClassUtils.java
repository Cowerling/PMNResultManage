package com.cowerling.pmn.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

public class ClassUtils {
    public static Method getMethod(Class<?> objectClass, String methodName) {
        for (Method method: objectClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    public static Method getMethod(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        for (Method method: objectClass.getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }

        return null;
    }

    public static Field getDeclaredField(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        for (Field field : objectClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                return field;
            }
        }

        return null;
    }

    public static <T> T getDeclaredFieldValue(Object object, Class<? extends Annotation> annotationClass) throws InvocationTargetException, IllegalAccessException {
        Field field = ClassUtils.getDeclaredField(object.getClass(), annotationClass);

        if (field == null) {
            return null;
        }

        Method method = ClassUtils.getMethod(object.getClass(), "get" + org.apache.commons.lang3.StringUtils.capitalize(field.getName()));

        if (method == null) {
            return null;
        }

        return (T) method.invoke(object);
    }

    public static <T> T getDeclaredFieldValue(Object object, Field field) throws InvocationTargetException, IllegalAccessException {
        Method method = ClassUtils.getMethod(object.getClass(), "get" + org.apache.commons.lang3.StringUtils.capitalize(field.getName()));

        if (method == null) {
            return null;
        }

        return (T) method.invoke(object);
    }

    public static void invokeMethod (Method method, Object object, Object value) throws IllegalAccessException, InvocationTargetException, ParseException {
        Class<?> parameterType = method.getParameterTypes()[0];

        Object parameter = null;
        if (parameterType.equals(Integer.class)) {
            parameter = Integer.parseInt(value.toString());
        } else if (parameterType.equals(Long.class)) {
            parameter = Long.parseLong(value.toString());
        } else if (parameterType.equals(Float.class)) {
            parameter = Float.parseFloat(value.toString());
        } else if (parameterType.equals(Double.class)) {
            parameter = Double.parseDouble(value.toString());
        } else if (parameterType.equals(String.class)) {
            parameter = value.toString();
        } else if (parameterType.equals(Date.class)) {
            parameter = DateUtils.parse(value.toString());
        }

        method.invoke(object, parameterType.cast(parameter));
    }
}
