package com.mcbbs.huade.reflectbuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectObjectBuilder implements ReflectBuilder{
    private Class<?> cla;
    private Object obj;

    public ReflectObjectBuilder(Object obj) {
        this.obj = obj;
        this.cla = obj.getClass();
    }
    public static ReflectObjectBuilder fromObject(Object obj){
        return new ReflectObjectBuilder(obj);
    }

    public Object toObject() {
        return this.obj;
    }

    @Override
    public ReflectObjectBuilder doMethod(String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] objArgs=args.clone();
        Class<?>[] claArgs=new Class[args.length];
        //处理参数
        for (int i=0; i<objArgs.length;i++){
            if (objArgs[i] instanceof ReflectBuilder){
                if (objArgs[i] instanceof ReflectClassBuilder){
                    objArgs[i]=((ReflectClassBuilder)objArgs[i]).toClass();
                }
                else if(objArgs[i] instanceof ReflectObjectBuilder){
                    objArgs[i]=((ReflectObjectBuilder)objArgs[i]).toObject();//将两种Builder转换为正常类型
                }
            }
        }
        for(int i=0; i<objArgs.length;i++){
            claArgs[i]=objArgs[i].getClass();
        }
        Method method;
        try {
            method=this.cla.getMethod(methodName,claArgs);
        }catch (NoSuchMethodException e){
            method=this.cla.getDeclaredMethod(methodName,claArgs);
            method.setAccessible(true);
        }
        return new ReflectObjectBuilder(method.invoke(this.obj,objArgs));
    }

    @Override
    public ReflectObjectBuilder getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field= this.cla.getField(fieldName);
        }catch (NoSuchFieldException e){
            field=this.cla.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        return new ReflectObjectBuilder(field.get(this.obj));
    }

    @Override
    public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field= this.cla.getField(fieldName);
        }catch (NoSuchFieldException e){
            field=this.cla.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        field.set(this.obj,value);
    }
}
