package com.imaginat.remindme.geofencing;

import com.imaginat.remindme.BasePresenter;
import com.imaginat.remindme.BaseView;

/**
 * Created by nat on 8/30/16.
 */
public class GeoFenceContract {


    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter, CoordinatesResultReceiver.ICoordinateReceiver{

    }
}
