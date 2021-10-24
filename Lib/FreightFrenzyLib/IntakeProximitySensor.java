package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

public class IntakeProximitySensor {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private NormalizedColorSensor intakeProximitySensor;
    private double noObjectInIntakeDistanceValue = 6;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public IntakeProximitySensor(HardwareMap hardwareMap) {
        intakeProximitySensor = hardwareMap.get(NormalizedColorSensor.class, UltimateGoalRobotRoadRunner.HardwareName.STAGE_1_SENSOR.hwName);
        if (intakeProximitySensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeProximitySensor).enableLight(true);
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
    public boolean isIntakeEmpty() {
        boolean result = false;
        if (intakeProximitySensor instanceof DistanceSensor) {
            if (((DistanceSensor) intakeProximitySensor).getDistance(DistanceUnit.CM) < noObjectInIntakeDistanceValue) {
                result = true;
            }
        }
        return result;
    }

}
