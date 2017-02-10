package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class SideBeaconPusher {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum SideBeaconPusherPosition {
        LEFT, RIGHT
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    SideBeaconPusherPosition sideBeaconPusherPosition;
    Servo8863 beaconServo;
    AdafruitColorSensor8863 beaconColorSensor;
    MuxPlusColorSensors muxPlusColorSensors;
    DriveTrain driveTrain;
    double homePosition = 0;
    double halfwayPosition = .25;
    double openPosition = .40;
    double extraPosition1 = 0;
    double extraPosition2 = 0;


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
    public SideBeaconPusher(HardwareMap hardwareMap, Telemetry telemetry, DriveTrain driveTrain, SideBeaconPusherPosition sideBeaconPusherPosition, MuxPlusColorSensors muxPlusColorSensors) {
        String beaconColorSensorName;
        int beaconColorSensorLEDPort;
        beaconServo = new Servo8863(RobotConfigMappingForGenericTest.getRightSideBeaconPusherServo(), hardwareMap, telemetry, homePosition, openPosition, homePosition, homePosition, Servo.Direction.FORWARD);
        this.driveTrain = driveTrain;

        if (sideBeaconPusherPosition == SideBeaconPusherPosition.LEFT) {
            beaconColorSensorName = RobotConfigMappingForGenericTest.getLeftSideBeaconColorSensorName();
            beaconColorSensorLEDPort = RobotConfigMappingForGenericTest.getLeftSideBeaconPusherColorSensorLEDPort();
            //
            beaconServo.setHomePosition(homePosition); //running against the wall
            beaconServo.setPositionOne(halfwayPosition); //scanning for beacon
            beaconServo.setPositionTwo(openPosition); //pushing button
        } else {
            beaconColorSensorName = RobotConfigMappingForGenericTest.getRightSideBeaconColorSensorName();
            beaconColorSensorLEDPort = RobotConfigMappingForGenericTest.getRightSideBeaconPusherColorSensorLEDPort();
            //
            beaconServo.setHomePosition(homePosition); //running against the wall
            beaconServo.setPositionOne(halfwayPosition); //scanning for beacon
            beaconServo.setPositionTwo(openPosition); //pushing button
        }
        this.muxPlusColorSensors = muxPlusColorSensors;
        beaconServo.goHome();
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

    public void init() {
        retractArm();
    }

    public void shutdown() {
        retractArm();
    }

    //--------------------------------
    //Methods for controlling the arm
    //--------------------------------

    public void retractArm () {
        beaconServo.goHome();
    }

    public void extendArmHalfWay () {
        beaconServo.goPositionOne();
    }

    public void extendingArmFully () {
        beaconServo.goPositionTwo();
    }

    //----------------------------------
    // Methods for driving
    //----------------------------------

    public void driveAlongWall (double heading, double power) {
        driveTrain.setupDriveUsingIMU (heading, power, AdafruitIMU8863.AngleMode.RELATIVE);
    }

    public double updateDriveAlongWall () {
        return driveTrain.updateDriveUsingIMU();
    }

    public void stopDriveAlongWall () {
        driveTrain.stopDriveUsingIMU();
    }

    public void driveNearBeacon (double heading, double maxPower) {
        driveTrain.setupDriveUsingIMU (heading, maxPower, AdafruitIMU8863.AngleMode.RELATIVE);
    }

    public void driveDistance (double distance, double power) {
        driveTrain.setupDriveDistanceUsingIMU(0, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 1000);
    }

    public boolean updateDriveDistance () {
        return driveTrain.updateDriveDistanceUsingIMU();
    }

    public void updateDriveNearBeacon () {
        driveTrain.updateDriveUsingIMU();
    }

    //-----------------------------------------------
    // Color Sensor Methods
    //-----------------------------------------------

    public boolean isRightSideBeaconRed () {
        return muxPlusColorSensors.rightSideBeaconPusherColorSensorIsRed();
    }

    public boolean isRightSideBeaconBlue () {
        return muxPlusColorSensors.rightSideBeaconPusherColorSensorIsBlue();
    }


    public boolean isLeftSideBeaconRed () {
        return muxPlusColorSensors.leftSideBeaconPusherColorSensorIsRed();
    }

    public boolean isLeftSideBeaconBlue () {
        return muxPlusColorSensors.leftSideBeaconPusherColorSensorIsBlue();
    }

    public AdafruitColorSensor8863.ColorFromSensor getRightSideBeaconColor() {
        return muxPlusColorSensors.getSimpleColorRightSideBeaconPushColorSensor();
    }

    public AdafruitColorSensor8863.ColorFromSensor getLeftSideBeaconColor() {
        return muxPlusColorSensors.getSimpleColorLeftSideBeaconPushColorSensor();
    }


    public void displayBeaconColor(Telemetry telemetry, MuxPlusColorSensors.BeaconSide side) {
        if (side == MuxPlusColorSensors.BeaconSide.RIGHT) {
            telemetry.addData("Beacon Color (right) = ", getRightSideBeaconColor().toString());
        } else {
            telemetry.addData("Beacon Color (left) = ", getLeftSideBeaconColor().toString());
        }

    }

    public String rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide side) {
        if (side == MuxPlusColorSensors.BeaconSide.RIGHT) {
            return muxPlusColorSensors.rightSideBeaconPusherColorSensorRGBValuesScaledAsString();
        } else {
            return muxPlusColorSensors.leftSideBeaconPusherColorSensorRGBValuesScaledAsString();
        }
    }


    public void setCoreDimLEDToMatchColorSensor(MuxPlusColorSensors.BeaconSide side) {
        muxPlusColorSensors.setCoreDimLedToMatchColorSensor(side);
    }

//    public void displayBeaconColor(Telemetry telemetry) {
//        telemetry.addData("Beacon Color (=", muxPlusColorSensors.);
//    }
}
