package com.university.oldenburg.mamksfzactivitytracker;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class ActivityTrackerApplication extends Application {

    File activityFile;
    File locationFile;
    private static ActivityTrackerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


    }

    public static ActivityTrackerApplication getInstance() {
        return instance;
    }
    public File getActivityFile() {
        return activityFile;
    }

    public void setActivityFile(File activityFile) {
        this.activityFile = activityFile;
    }

    public File getLocationFile() {
        return locationFile;
    }

    public void setLocationFile(File locationFile) {
        this.locationFile = locationFile;
    }




}
