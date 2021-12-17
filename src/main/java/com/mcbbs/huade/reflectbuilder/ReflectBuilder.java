package com.mcbbs.huade.reflectbuilder;

import java.lang.reflect.InvocationTargetException;

public interface ReflectBuilder {
    ReflectObjectBuilder doMethod(String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    ReflectObjectBuilder getField(String fieldName) throws NoSuchFieldException, IllegalAccessException;
    void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException;
}
