package com.batch57.gdipsa.group6.lapsbackend.model.enumLayer;

public enum MEDICAL_LEAVE_MAXIMUM {
    MAXIMUM(60);
    private Integer value;

    MEDICAL_LEAVE_MAXIMUM(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
