package com.qzc.mobiledlsearch.entity;

import java.util.List;

public class ParameterBean {
    private List<String> applicationDomain;
    private List<String> applicationArea;
    private List<String> dataSourceType;
    private List<String> dataFeature;
    private List<String> modelBackend;
    private List<String> modelType;
    private List<String> modelLossFunction;
    private List<String> modelOptimiser;
    private List<String> coreLayer;
    private List<String> functionalLayer;
    private String accuracy;
    private String precision;
    private String recall;
    private String f1score;

    public ParameterBean() {
    }

    public ParameterBean(List<String> applicationDomain, List<String> applicationArea, List<String> dataSourceType, List<String> dataFeature, List<String> modelBackend, List<String> modelType, List<String> modelLossFunction, List<String> modelOptimiser, List<String> coreLayer, List<String> functionalLayer, String accuracy, String precision, String recall, String f1score) {
        this.applicationDomain = applicationDomain;
        this.applicationArea = applicationArea;
        this.dataSourceType = dataSourceType;
        this.dataFeature = dataFeature;
        this.modelBackend = modelBackend;
        this.modelType = modelType;
        this.modelLossFunction = modelLossFunction;
        this.modelOptimiser = modelOptimiser;
        this.coreLayer = coreLayer;
        this.functionalLayer = functionalLayer;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1score = f1score;
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

    public List<String> getDataFeature() {
        return dataFeature;
    }

    public void setDataFeature(List<String> dataFeature) {
        this.dataFeature = dataFeature;
    }

    public List<String> getModelBackend() {
        return modelBackend;
    }

    public void setModelBackend(List<String> modelBackend) {
        this.modelBackend = modelBackend;
    }

    public List<String> getModelType() {
        return modelType;
    }

    public void setModelType(List<String> modelType) {
        this.modelType = modelType;
    }

    public List<String> getModelLossFunction() {
        return modelLossFunction;
    }

    public void setModelLossFunction(List<String> modelLossFunction) {
        this.modelLossFunction = modelLossFunction;
    }

    public List<String> getModelOptimiser() {
        return modelOptimiser;
    }

    public void setModelOptimiser(List<String> modelOptimiser) {
        this.modelOptimiser = modelOptimiser;
    }

    public List<String> getCoreLayer() {
        return coreLayer;
    }

    public void setCoreLayer(List<String> coreLayer) {
        this.coreLayer = coreLayer;
    }

    public List<String> getFunctionalLayer() {
        return functionalLayer;
    }

    public void setFunctionalLayer(List<String> functionalLayer) {
        this.functionalLayer = functionalLayer;
    }

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
}
