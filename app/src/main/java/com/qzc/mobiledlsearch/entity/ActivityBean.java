package com.qzc.mobiledlsearch.entity;

public class ActivityBean {
    private String activityName;
    private float activityProbability;

    public String getActivityName() {
        return activityName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public float getActivityProbability() {
        return activityProbability;
    }
    public void setActivityProbability(float activityProbability) {
        this.activityProbability = activityProbability;
    }

    public ActivityBean(String activityName, float activityProbability) {
        super();
        this.activityName = activityName;
        this.activityProbability = activityProbability;
    }

    public ActivityBean() {
        super();
    }
}
