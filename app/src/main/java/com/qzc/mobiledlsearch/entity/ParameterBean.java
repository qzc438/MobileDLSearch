package com.qzc.mobiledlsearch.entity;

import java.util.List;

public class ParameterBean {
    private List<String> applicationDomain;
    private List<String> applicationArea;
    private List<String> dataSourceType;
    private List<String> modelType;
    private String accuracy;
    private String precision;
    private String recall;
    private String f1score;

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getRecall() {
        return recall;
    }

    public void setRecall(String recall) {
        this.recall = recall;
    }

    public String getF1score() {
        return f1score;
    }

    public void setF1score(String f1score) {
        this.f1score = f1score;
    }

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
