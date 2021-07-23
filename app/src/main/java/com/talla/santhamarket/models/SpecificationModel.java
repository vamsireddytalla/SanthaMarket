package com.talla.santhamarket.models;

import java.io.Serializable;
import java.util.Map;

public class SpecificationModel implements Serializable
{
    Map<String,Object> specMap;

    public Map<String, Object> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, Object> specMap) {
        this.specMap = specMap;
    }

    @Override
    public String toString() {
        return "SpecificationModel{" +
                "specMap=" + specMap +
                '}';
    }
}
