package com.qzc.mobiledlsearch.entity;

import java.util.List;

public class ParameterBean {
    private List<String> applicationDomain;
    private List<String> applicationArea;
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
}
