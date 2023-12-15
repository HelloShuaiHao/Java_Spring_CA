package com.batch57.gdipsa.group6.lapsbackend.model.enumLayer;

public enum OVERWORKING_UNIT {
    UNIT(4);

    private Integer value;

    OVERWORKING_UNIT(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
