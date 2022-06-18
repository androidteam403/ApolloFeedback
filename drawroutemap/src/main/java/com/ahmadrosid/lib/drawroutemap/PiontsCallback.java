package com.ahmadrosid.lib.drawroutemap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public interface PiontsCallback {
   void pointsFirst(List<LatLng> pionts);
   void pointsSecond(List<LatLng> pionts);
}
