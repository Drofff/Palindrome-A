package com.drofff.palindrome.type;

import java.lang.reflect.Field;

public class FieldValue {

    private Field field;

    private Object value;

    public FieldValue(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
