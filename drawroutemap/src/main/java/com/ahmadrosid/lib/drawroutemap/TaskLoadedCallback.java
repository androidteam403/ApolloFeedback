package com.ahmadrosid.lib.drawroutemap;

import com.google.android.gms.maps.model.Polyline;

public interface TaskLoadedCallback {
    Polyline onTaskDone(boolean flag, Object... values);

    Polyline onSecondTaskDone(boolean flag, Object... values);
}
