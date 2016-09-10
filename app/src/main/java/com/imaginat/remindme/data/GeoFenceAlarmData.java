package com.imaginat.remindme.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.imaginat.remindme.data.source.local.DBSchema;

/**
 * Created by nat on 8/31/16.
 */
public class GeoFenceAlarmData implements Parcelable {
    private String mAlarmID;

    public GeoFenceAlarmData(){

    }

    public GeoFenceAlarmData(final GeoFenceAlarmData geoData){
        mAlarmID = geoData.getAlarmID();
        mReminderID = geoData.getReminderID();
        mStreet = geoData.getStreet();
        mCity = geoData.getCity();
        mState = geoData.getState();
        mZipcode = geoData.getZipcode();
        mLongitude = geoData.getLongitude();
        mLatitude = geoData.getLatitude();
        mMeterRadius = geoData.getMeterRadius();
        mAlarmTag = geoData.getAlarmTag();
        mIsActive = geoData.isActive();
    }
    protected GeoFenceAlarmData(Parcel in) {
        mAlarmID = in.readString();
        mReminderID = in.readString();
        mStreet = in.readString();
        mCity = in.readString();
        mState = in.readString();
        mZipcode = in.readString();
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
        mMeterRadius = in.readInt();
        mAlarmTag = in.readString();
        mIsActive = in.readByte() != 0;
    }

    public ContentValues getAsContentValues(){

        ContentValues contentValues = new ContentValues();
        //hashMapValues.put(DBSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID,mAlarmID);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.REMINDER_ID,mReminderID);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.STREET,mStreet);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.CITY,mCity);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.STATE,mState);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.ZIPCODE,mZipcode);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.LATITUDE,Double.toString(mLatitude));
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.LONGITUDE,Double.toString(mLongitude));
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.RADIUS,Integer.toString(mMeterRadius));
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.ALARM_TAG,mAlarmTag);
        contentValues.put(DBSchema.geoFenceAlarm_table.cols.IS_ACTIVE,Integer.toString(mIsActive?1:0));
        return contentValues;

    }
    public static final Creator<GeoFenceAlarmData> CREATOR = new Creator<GeoFenceAlarmData>() {
        @Override
        public GeoFenceAlarmData createFromParcel(Parcel in) {
            return new GeoFenceAlarmData(in);
        }

        @Override
        public GeoFenceAlarmData[] newArray(int size) {
            return new GeoFenceAlarmData[size];
        }
    };

    public String getReminderID() {
        return mReminderID;
    }

    public void setReminderID(String reminderID) {
        mReminderID = reminderID;
    }

    private String mReminderID;
    private String mStreet,mCity,mState,mZipcode;
    private double mLongitude, mLatitude;
    private int mMeterRadius=100;
    private String mAlarmTag;
    private boolean mIsActive;

    public String getAlarmID() {
        return mAlarmID;
    }

    public void setAlarmID(String alarmID) {
        mAlarmID = alarmID;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        mZipcode = zipcode;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public int getMeterRadius() {
        return mMeterRadius;
    }

    public void setMeterRadius(int meterRadius) {
        mMeterRadius = meterRadius;
    }

    public String getAlarmTag() {
        return mAlarmTag;
    }

    public void setAlarmTag(String alarmTag) {
        mAlarmTag = alarmTag;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAlarmID);
        parcel.writeString(mReminderID);
        parcel.writeString(mStreet);
        parcel.writeString(mCity);
        parcel.writeString(mState);
        parcel.writeString(mZipcode);
        parcel.writeDouble(mLongitude);
        parcel.writeDouble(mLatitude);
        parcel.writeInt(mMeterRadius);
        parcel.writeString(mAlarmTag);
        parcel.writeByte((byte) (mIsActive ? 1 : 0));
    }
}
