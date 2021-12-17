package com.mcbbs.huade.reflectbuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectClassBuilder implements ReflectBuilder{
    private Class<?> cla;
    public ReflectClassBuilder(Class<?> cla) {
        this.cla = cla;
    }
    public ReflectClassBuilder(String name) throws ClassNotFoundException {
        this.cla =Class.forName(name);
    }
    public static ReflectClassBuilder forName(String name) throws ClassNotFoundException {
        return new ReflectClassBuilder(name);
    }
    public Class<?> toClass(){
        return this.cla;
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
        return new ReflectObjectBuilder(method.invoke(null,objArgs));

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
        return new ReflectObjectBuilder(field.get(null));
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
        field.set(null,value);
    }
    public ReflectObjectBuilder newInstance(Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
        Constructor<?> constructor;
        try {
            constructor =this.cla.getConstructor(claArgs);//先假设是public
        } catch (NoSuchMethodException e) {
            constructor = this.cla.getDeclaredConstructor(claArgs);
            constructor.setAccessible(true);//作为最后的尝试假设是私有权限
        }
        return new ReflectObjectBuilder(constructor.newInstance(objArgs));
    }
}
