package com.ahmadrosid.lib.drawroutemap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ocittwo on 11/14/16.
 *
 * @Author Ahmad Rosid
 * @Email ocittwo@gmail.com
 * @Github https://github.com/ar-android
 * @Web http://ahmadrosid.com
 */

public class DrawRouteMaps {

    private static DrawRouteMaps instance;
    private Context context;
    private DirectionApiCallback directionApiCallback;
    private TaskLoadedCallback taskLoadedCallback;
    private PiontsCallback piontsCallback;

    public static DrawRouteMaps getInstance(Context context, DirectionApiCallback directionApiCallback, TaskLoadedCallback taskLoadedCallback, PiontsCallback piontsCallback) {
        instance = new DrawRouteMaps();
        instance.context = context;
        instance.directionApiCallback = directionApiCallback;
        instance.taskLoadedCallback = taskLoadedCallback;
        instance.piontsCallback = piontsCallback;
        return instance;
    }

    public DrawRouteMaps draw(LatLng origin, LatLng destination, GoogleMap googleMap, int colorFlag){
        String url_route = FetchUrl.getUrl(origin, destination);
        DrawRoute drawRoute = new DrawRoute(googleMap, colorFlag, directionApiCallback, taskLoadedCallback, piontsCallback);
        drawRoute.execute(url_route);
        return instance;
    }

    public static Context getContext() {
        return instance.context;
    }
}
