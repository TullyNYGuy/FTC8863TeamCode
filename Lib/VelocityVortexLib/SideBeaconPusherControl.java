package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwareK9bot;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

public class SideBeaconPusherControl {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum SideBeaconPusherState {
        RUNNING_ALONG_THE_WALL,
        BEACON_DETECTED,
        PUSH_BUTTON,
        DRIVE_COMPLETE,
        SKIP_BUTTON,
        DRIVE_FORWARD_AFTER_BEACON,
        DETECT_AFTER_SKIP,
        PUSH_AFTER_SKIP,
        RETRACT_ARM_AFTER_SKIP,
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private SideBeaconPusher sideBeaconPusher;
    private SideBeaconPusherState sideBeaconPusherState;
    private VelocityVortexRobot.AllianceColor allianceColor;

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

    public SideBeaconPusherControl(HardwareMap hardwareMap, Telemetry telemetry, DriveTrain
            driveTrain, SideBeaconPusher.SideBeaconPusherPosition sideBeaconPusherPosition,
            VelocityVortexRobot.AllianceColor allianceColor) {
        this.sideBeaconPusher = new SideBeaconPusher(hardwareMap, telemetry, driveTrain, sideBeaconPusherPosition);
        this.allianceColor = allianceColor;
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
    public SideBeaconPusherState update () {
        switch (sideBeaconPusherState){
            case RUNNING_ALONG_THE_WALL:
                sideBeaconPusher.driveAlongWall();
                sideBeaconPusher.extendArmHalfWay();
                if (sideBeaconPusher.isBeaconBlue() || sideBeaconPusher.isBeaconRed()){
                    sideBeaconPusherState = SideBeaconPusherState.BEACON_DETECTED;
                }
                break;
            case  BEACON_DETECTED:
                sideBeaconPusher.driveNearBeacon();
                if (sideBeaconPusher.isBeaconBlue() && allianceColor == VelocityVortexRobot.AllianceColor.BLUE ||
                 sideBeaconPusher.isBeaconRed() && allianceColor == VelocityVortexRobot.AllianceColor.RED ) {
                    sideBeaconPusherState = SideBeaconPusherState.PUSH_BUTTON;
                } else {
                    sideBeaconPusherState = SideBeaconPusherState.SKIP_BUTTON;
                }
                break;
            //CURRENTLY SETTING DRIVING AND DISTANCE
            case PUSH_BUTTON:
                sideBeaconPusher.extendingArmFully();
                sideBeaconPusher.
                break;
            case DRIVE_COMPLETE:
                break;


            case SKIP_BUTTON:
                sideBeaconPusher.retractArm();
                break;
            case DRIVE_FORWARD_AFTER_BEACON:
                break;
            case DETECT_AFTER_SKIP:
                break;
            case PUSH_AFTER_SKIP:
                break;
            case RETRACT_ARM_AFTER_SKIP:
                break;
        }
    }
}
