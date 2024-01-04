package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CenterStageIntakeColorSensorRight {

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
    private CenterStageColorSensorForDistance rightColorSensor;
    private final String INTAKE_RIGHT_COLOR_SENSOR_NAME = "intakeRightColorSensor";
    
    private double distanceThreshold = 1.0; // cm
    private DistanceUnit distanceUnit = DistanceUnit.CM;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public CenterStageIntakeColorSensorRight(HardwareMap hardwareMap, Telemetry telemetry) {
        rightColorSensor = new CenterStageColorSensorForDistance(hardwareMap, telemetry,
                INTAKE_RIGHT_COLOR_SENSOR_NAME,
                distanceThreshold,
                distanceUnit);
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
    
    public boolean isPixelPresent() {
        return rightColorSensor.isObjectPresent();
    }
}
