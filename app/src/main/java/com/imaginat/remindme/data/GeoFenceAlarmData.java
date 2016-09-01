package com.imaginat.remindme.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nat on 8/31/16.
 */
public class GeoFenceAlarmData implements Parcelable {
    private String mAlarmID;

    public GeoFenceAlarmData(){

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
    private int mMeterRadius;
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
