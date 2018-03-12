package edu.temple.albertjbelardino.publickeyfriends;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by albertjbelardino on 3/4/2018.
 */

public class Partner implements Comparable<Partner>, Serializable {
    Double latitude;
    Double longitude;
    Location location;
    String userName;

    public Partner(String userName, Double latitude, Double longitude) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    public Partner() {
        this.location = null;
        this.userName = null;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int compareTo(@NonNull Partner partner) {
        Location center = new Location(LocationManager.NETWORK_PROVIDER);
        center.setLatitude(0);
        center.setLongitude(0);

        if(this.location.distanceTo(center) > partner.location.distanceTo(center))
            return 1;
        else
            return -1;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
