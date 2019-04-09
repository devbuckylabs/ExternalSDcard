package com.buckylabs.externalsdcard;

import android.graphics.drawable.Drawable;

public class Apk {
    private String AppName;
    private String AppPackage;
    private String AppStatus;
    private String AppVersionName;
    private String date;
    private String AppSize;
    private String sourceDirectory;
    private boolean isChecked;
    private Drawable AppIcon;


    public Apk(String appName, Drawable appIcon,String appPackage, String appStatus, String appVersionName, String date, String appSize, String sourceDirectory, boolean isChecked ) {
        AppPackage = appPackage;
        AppStatus = appStatus;
        AppVersionName = appVersionName;
        this.date = date;
        AppSize = appSize;
        this.sourceDirectory = sourceDirectory;
        this.isChecked = isChecked;
        AppName = appName;
        AppIcon = appIcon;
    }



    public String getAppPackage() {
        return AppPackage;
    }

    public void setAppPackage(String appPackage) {
        AppPackage = appPackage;
    }

    public String getAppStatus() {
        return AppStatus;
    }

    public void setAppStatus(String appStatus) {
        AppStatus = appStatus;
    }

    public String getAppVersionName() {
        return AppVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        AppVersionName = appVersionName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAppSize() {
        return AppSize;
    }

    public void setAppSize(String appSize) {
        AppSize = appSize;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }



    @Override
    public String toString() {
        return "Apk{" +
                "AppPackage='" + AppPackage + '\'' +
                ", AppStatus='" + AppStatus + '\'' +
                ", AppVersionName='" + AppVersionName + '\'' +
                ", date='" + date + '\'' +
                ", AppSize='" + AppSize + '\'' +
                ", sourceDirectory='" + sourceDirectory + '\'' +
                ", isChecked=" + isChecked +
                ", AppName='" + AppName + '\'' +
                ", AppIcon=" + AppIcon +
                '}';
    }


}