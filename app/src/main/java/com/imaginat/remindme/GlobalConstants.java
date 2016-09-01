package com.imaginat.remindme;

/**
 * Created by nat on 8/8/16.
 */
public class GlobalConstants {
    public static final int HIDE_COMPLETED_ITEMS=2340;

    //GENERAL
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;


    public static final String PACKAGE_NAME = "com.imaginat.remindme";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";



    //ICONS FOR THE LIST
    public static final int GENERIC_REMINDER_ICON=0;
    public static final int SHOPPING_CART_ICON=1;
    public static final int WORKOUT_ICON=2;
    public static final int WORK_ICON=3;
    public static final int KIDS_ICON=4;
    public static final int BEACH_ICON=5;
    public static final int TOTAL_ICONS=6;

    //LIST ID KEY (used when passing data)
    public static final String CURRENT_LIST_ID="currentListID";
    public static final String CURRENT_TASK_ID="currentItemID";

    //GEO ALARM
    public static final String GEO_ALARM_DATA_EXTRA=PACKAGE_NAME+"GeoFenceAlarm_data";
    public static final String ALARM_TAG=PACKAGE_NAME+"ALARM_TAG";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final String INTENT_SOURCE = "sourceOfThisIntent";
    public static final String LIST_OF_TRIGGERED="triggeredGeoFences";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200; // 1 mile, 1.6 km




}
