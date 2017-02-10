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
        IDLE,
        RUNNING_ALONG_THE_WALL,
        SEARCHING_FOR_BEACON,
        BEACON_DETECTED,
        PUSH_BUTTON,
        DRIVE_PAST_BEACON,
        SKIP_BUTTON,
        DRIVE_TO_SECOND_BUTTON,
        PUSH_SECOND_BUTTON,
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
    private AllianceColorSwitch.AllianceColor allianceColor;
    private int beaconCount = 1;
    MuxPlusColorSensors muxPlusColorSensors;

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

    public SideBeaconPusherControl(HardwareMap hardwareMap, Telemetry telemetry,
                                   MuxPlusColorSensors muxPlusColorSensors,
                                   DriveTrain driveTrain,
                                   SideBeaconPusher.SideBeaconPusherPosition sideBeaconPusherPosition,
                                   AllianceColorSwitch.AllianceColor allianceColor) {
        this.sideBeaconPusher = new SideBeaconPusher(hardwareMap, telemetry, driveTrain, sideBeaconPusherPosition, muxPlusColorSensors);
        this.allianceColor = allianceColor;
        this.telemetry = telemetry;
        sideBeaconPusherState = SideBeaconPusherState.IDLE;
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

    //----------------------------------------------------
    //  Commands
    //----------------------------------------------------

    // before running this method navigate to the wall
    public void  startSideBeaconPusherControl() {
        sideBeaconPusherState = SideBeaconPusherState.RUNNING_ALONG_THE_WALL;
        update();
    }

    //----------------------------------------------------
    //  STATE MACHINE
    //----------------------------------------------------

    public SideBeaconPusherState update() {
        switch (sideBeaconPusherState) {
            case IDLE:
                break;
            case RUNNING_ALONG_THE_WALL:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.driveAlongWall(0, .1);
                sideBeaconPusher.extendArmHalfWay();
                sideBeaconPusherState = SideBeaconPusherState.SEARCHING_FOR_BEACON;
                break;
            case SEARCHING_FOR_BEACON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.updateDriveAlongWall();
                //check if we have passed a beacon
                if (sideBeaconPusher.isRightSideBeaconBlue() || sideBeaconPusher.isRightSideBeaconRed()) {
                    sideBeaconPusher.stopDriveAlongWall();
                    sideBeaconPusherState = SideBeaconPusherState.BEACON_DETECTED;
                }
                break;
            case BEACON_DETECTED:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                if (sideBeaconPusher.isRightSideBeaconBlue() && allianceColor == AllianceColorSwitch.AllianceColor.BLUE ||
                        sideBeaconPusher.isRightSideBeaconRed() && allianceColor == AllianceColorSwitch.AllianceColor.RED) {
                    sideBeaconPusherState = SideBeaconPusherState.PUSH_BUTTON;
                } else {
                    sideBeaconPusherState = SideBeaconPusherState.SKIP_BUTTON;
                }
                break;
            case PUSH_BUTTON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                sideBeaconPusher.extendingArmFully();
                sideBeaconPusher.driveDistance(0, .1);
                sideBeaconPusherState = SideBeaconPusherState.DRIVE_PAST_BEACON;
                break;

            case DRIVE_PAST_BEACON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                if (sideBeaconPusher.updateDriveDistance()) {
                    sideBeaconPusher.retractArm();
                    sideBeaconPusher.driveAlongWall(0, .1);
                    sideBeaconPusherState = SideBeaconPusherState.DRIVE_FORWARD_AFTER_BEACON_PUSH;
                }
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
                sideBeaconPusher.driveDistance(0, .1);
                sideBeaconPusherState = SideBeaconPusherState.DRIVE_TO_SECOND_BUTTON;
                break;

            case DRIVE_TO_SECOND_BUTTON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                if (sideBeaconPusher.updateDriveDistance()) {
                    sideBeaconPusher.extendingArmFully();
                    sideBeaconPusher.driveDistance(0, .5);
                }
                sideBeaconPusherState = SideBeaconPusherState.PUSH_SECOND_BUTTON;
                break;

            case PUSH_SECOND_BUTTON:
                telemetry.addData("State =", sideBeaconPusherState.toString());
                telemetry.update();
                if (sideBeaconPusher.updateDriveDistance()) {
                    sideBeaconPusherState = SideBeaconPusherState.RUNNING_ALONG_THE_WALL;
                }
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