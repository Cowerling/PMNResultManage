package com.cowerling.pmn.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtils {
    public static Method getMethod(Class<?> objectClass, String methodName) {
        for (Method method: objectClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    public static void invokeMethod(Method method,Object object, Object value) throws IllegalAccessException, InvocationTargetException {
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
        }

        method.invoke(object, parameterType.cast(parameter));
    }
}
