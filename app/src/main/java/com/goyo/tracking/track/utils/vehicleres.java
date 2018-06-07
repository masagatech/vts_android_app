package com.goyo.tracking.track.utils;

import com.goyo.tracking.track.R;
import com.goyo.tracking.track.enums.CarStatus;

public class vehicleres {

    public static int getVehIcon(String icon, CarStatus status) {
        if (icon == null) icon = "";
        switch (icon) {
            case "car": {
                if (status.equals(CarStatus.Green)) return R.drawable.vhico_car_green;
                else if (status.equals(CarStatus.Red)) return R.drawable.vhico_car_red;
                else if (status.equals(CarStatus.Yello)) return R.drawable.vhico_car_yelow;
            }
            break;
            case "bus": {
                if (status.equals(CarStatus.Green)) return R.drawable.vhico_bus_green;
                else if (status.equals(CarStatus.Red)) return R.drawable.vhico_bus_red;
                else if (status.equals(CarStatus.Yello)) return R.drawable.vhico_bus_yelow;
            }
            break;
            case "bike": {
                if (status.equals(CarStatus.Green)) return R.drawable.vhico_bike_green;
                else if (status.equals(CarStatus.Red)) return R.drawable.vhico_bike_red;
                else if (status.equals(CarStatus.Yello)) return R.drawable.vhico_bike_yelow;
            }
            default:
                if (status.equals(CarStatus.Green)) return R.drawable.green;
                else if (status.equals(CarStatus.Red)) return R.drawable.redcar;
                else if (status.equals(CarStatus.Yello)) return R.drawable.yellowcar;
                break;
        }
        return 0;
    }


}

