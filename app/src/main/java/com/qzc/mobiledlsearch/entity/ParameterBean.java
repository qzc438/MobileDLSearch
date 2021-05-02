package com.qzc.mobiledlsearch.entity;

import java.util.List;

public class ParameterBean {
    private List<String> applicationDomain;
    private List<String> applicationArea;
    private List<String> dataSourceType;
    private List<String> modelType;

    public List<String> getModelType() {
        return modelType;
    }

    public void setModelType(List<String> modelType) {
        this.modelType = modelType;
    }

    public List<String> getApplicationDomain() {
        return applicationDomain;
    }

    public void setApplicationDomain(List<String> applicationDomain) {
        this.applicationDomain = applicationDomain;
    }

    public List<String> getApplicationArea() {
        return applicationArea;
    }

    public void setApplicationArea(List<String> applicationArea) {
        this.applicationArea = applicationArea;
    }

    public List<String> getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(List<String> dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
