package com.goyo.tracking.tracking.utils;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.enums.CarStatus;

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
            break;
            case "truck": {
                if (status.equals(CarStatus.Green)) return R.drawable.vhico_bike_green;
                else if (status.equals(CarStatus.Red)) return R.drawable.vhico_bike_red;
                else if (status.equals(CarStatus.Yello)) return R.drawable.vhico_bike_yelow;
            }
            break;
            case "tractor": {
                if (status.equals(CarStatus.Green)) return R.drawable.vhico_tractor_green;
                else if (status.equals(CarStatus.Red)) return R.drawable.vhico_tractor_red;
                else if (status.equals(CarStatus.Yello)) return R.drawable.vhico_tractor_yellow;
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

