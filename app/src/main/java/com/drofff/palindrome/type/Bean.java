package com.drofff.palindrome.type;

public class Bean {

    private Object instance;

    private Class<?> clazz;

    public Bean(Object instance, Class<?> clazz) {
        this.instance = instance;
        this.clazz = clazz;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

}
