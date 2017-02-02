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
        SEARCHING_FOR_BEACON,
        BEACON_DETECTED,
        PUSH_BUTTON,
        DRIVE_COMPLETE,
        SKIP_BUTTON,
        DRIVE_FORWARD_AFTER_BEACON_PUSH,
        FINISHED
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Telemetry telemetry;
    private SideBeaconPusher sideBeaconPusher;
    private SideBeaconPusherState sideBeaconPusherState;
    private VelocityVortexRobot.AllianceColor allianceColor;
    private int beaconCount = 1;

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
        this.telemetry = telemetry;
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
    public SideBeaconPusherState update() {
        switch (sideBeaconPusherState) {
            case RUNNING_ALONG_THE_WALL:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.driveAlongWall(0, .6);
                sideBeaconPusher.extendArmHalfWay();
                sideBeaconPusherState = SideBeaconPusherState.SEARCHING_FOR_BEACON;
                break;
            case SEARCHING_FOR_BEACON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.updateDriveAlongWall();
                if (sideBeaconPusher.isBeaconBlue() || sideBeaconPusher.isBeaconRed()) {
                    sideBeaconPusher.stopDriveAlongWall();
                    sideBeaconPusherState = SideBeaconPusherState.BEACON_DETECTED;
                }
                break;
            case BEACON_DETECTED:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                if (sideBeaconPusher.isBeaconBlue() && allianceColor == VelocityVortexRobot.AllianceColor.BLUE ||
                        sideBeaconPusher.isBeaconRed() && allianceColor == VelocityVortexRobot.AllianceColor.RED) {
                    sideBeaconPusherState = SideBeaconPusherState.PUSH_BUTTON;
                } else {
                    sideBeaconPusherState = SideBeaconPusherState.SKIP_BUTTON;
                }
                break;
            case PUSH_BUTTON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.extendingArmFully();
                sideBeaconPusher.driveDistance(0, .3);
                sideBeaconPusherState = SideBeaconPusherState.DRIVE_COMPLETE;
                break;

            case DRIVE_COMPLETE:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.retractArm();
                sideBeaconPusher.driveAlongWall(0, .3);
                sideBeaconPusherState = SideBeaconPusherState.DRIVE_FORWARD_AFTER_BEACON_PUSH;
                break;

            case DRIVE_FORWARD_AFTER_BEACON_PUSH:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                beaconCount = beaconCount + 1;
                if (beaconCount == 2) {
                    sideBeaconPusherState = SideBeaconPusherState.FINISHED;
                } else {
                    sideBeaconPusherState = SideBeaconPusherState.RUNNING_ALONG_THE_WALL;
                }
                break;

            case SKIP_BUTTON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.retractArm();
                sideBeaconPusher.driveDistance(0, .3);
                sideBeaconPusherState = SideBeaconPusherState.SEARCHING_FOR_BEACON;
                break;

            case FINISHED:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                break;
            }
        return sideBeaconPusherState;
        }
    }
//IDEA use telemetry to print out the state we are currently in on the phone that way we can debug easier
// ADD ALL DISTANCES