package com.arnab.ambucyclerider.Callback;

import com.arnab.ambucyclerider.Model.DriverGeoModel;

public interface IFirebaseDriverInfoListener {
    void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel);
}
