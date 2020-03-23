package com.drofff.palindrome.type;

public class Bean {

    private Object bean;

    private Class<?> clazz;

    public Bean(Object bean, Class<?> clazz) {
        this.bean = bean;
        this.clazz = clazz;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

}
