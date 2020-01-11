package cz.uhk.fim.runhk.utils;

import android.graphics.Color;

public class PolylineUtils {

    private PolylineUtils() {
    }

    public static int getRouteColor(int routeIndex) {
        int color = 0;
        switch (routeIndex) {
            case 1:
                color = Color.BLUE;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.YELLOW;
                break;
        }
        return color;
    }
}
