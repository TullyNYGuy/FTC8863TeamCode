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
    DriveTrain driveTrain;
    double homePosition = .55;
    double halfwayPosition = .75;
    double openPosition = 1.0;
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
    public SideBeaconPusher(HardwareMap hardwareMap, Telemetry telemetry, DriveTrain driveTrain, SideBeaconPusherPosition sideBeaconPusherPosition) {
        String beaconColorSensorName;
        int beaconColorSensorLEDPort;
        beaconServo = new Servo8863(RobotConfigMappingForGenericTest.getRightSideBeaconPusherServo(), hardwareMap, telemetry, homePosition, openPosition, extraPosition1, extraPosition2, Servo.Direction.FORWARD);
        this.driveTrain = driveTrain;

        if (sideBeaconPusherPosition == SideBeaconPusherPosition.LEFT) {
            beaconColorSensorName = RobotConfigMappingForGenericTest.getLeftSideBeaconColorSensorName();
            beaconColorSensorLEDPort = RobotConfigMappingForGenericTest.getLeftSideBeaconColorSensorLEDPort();
            //
            beaconServo.setHomePosition(homePosition); //running against the wall
            beaconServo.setPositionOne(halfwayPosition); //scanning for beacon
            beaconServo.setPositionTwo(openPosition); //pushing button
        } else {
            beaconColorSensorName = RobotConfigMappingForGenericTest.getRightSideBeaconColorSensorName();
            beaconColorSensorLEDPort = RobotConfigMappingForGenericTest.getRightSideBeaconColorSensorLEDPort();
            //
            beaconServo.setHomePosition(homePosition); //running against the wall
            beaconServo.setPositionOne(halfwayPosition); //scanning for beacon
            beaconServo.setPositionTwo(openPosition); //pushing button
        }
        //beaconColorSensor = new AdafruitColorSensor8863(hardwareMap, beaconColorSensorName, RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(), beaconColorSensorLEDPort);
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
    // drive near beacon,
    //Methods for controlling the arm
    public void retractArm () {
        beaconServo.goHome();
    }
    public void extendArmHalfWay () {
        beaconServo.goPositionOne();
    }
    public void extendingArmFully () {
        beaconServo.goPositionTwo();
    }
    public boolean isBeaconRed () {
        return beaconColorSensor.isRedUsingRGB();
    }
    public boolean isBeaconBlue () {
        return beaconColorSensor.isBlueUsingRGB();
    }
    public void driveAlongWall (double heading, double power) {
        driveTrain.setupDriveUsingIMU (heading, power, AdafruitIMU8863.AngleMode.RELATIVE);
    }
    public double updateDriveAlongWall () {
        return driveTrain.updateDriveUsingIMU();
    }
    public void stopDriveAlongWall () {
        driveTrain.stopDriveUsingIMU();
    }
    public void driveNearBeacon () {
        driveTrain.setupDriveUsingIMU (0, .4, AdafruitIMU8863.AngleMode.RELATIVE);
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
}
