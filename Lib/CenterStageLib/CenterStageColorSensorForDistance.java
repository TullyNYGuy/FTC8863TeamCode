package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CenterStageColorSensorForDistance {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private NormalizedColorSensor colorSensor;

    private double distance = 0;

    public double getDistance() {
        return distance;
    }

    private double distanceThreshold = 0;

    public void setDistanceThreshold(double distanceThreshold, DistanceUnit distanceUnit) {
        this.distanceThreshold = distanceThreshold;
        this.distanceUnit = distanceUnit;
    }

    private DistanceUnit distanceUnit = DistanceUnit.CM;

    private String colorSensorName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStageColorSensorForDistance(HardwareMap hardwareMap, Telemetry telemetry,
                                             String colorSensorName,
                                             double distanceThreshold,
                                             DistanceUnit distanceUnit) {
        this.colorSensorName = colorSensorName;
        this.distanceThreshold = distanceThreshold;
        this.distanceUnit = distanceUnit;

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, colorSensorName);

        // turn on the led
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public boolean isObjectPresent() {
        if (colorSensor instanceof DistanceSensor) {
            distance = ((DistanceSensor) colorSensor).getDistance(distanceUnit);
        }
        if (distance < distanceThreshold) {
            return true;
        } else {
            return false;
        }
    }
}
