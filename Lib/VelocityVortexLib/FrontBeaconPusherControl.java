package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FrontBeaconPusherControl {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum FrontBeaconControlState {
        LOOK_FOR_BEACON,
        MOVE_PUSHERS_TO_MIDDLE,
        BOTH_BACK,
        AT_MIDDLE,
        BOTH_FORWARD,
        AT_LEFT_BACK_RIGHT_FORWARD,
        CHECK_BEACON_PUSHED,
        MOVING_LEFT_FORWARD_RIGHT_BACK,
        MOVING_RIGHT_FORWARD_LEFT_BACK,
        AT_RIGHT_BACK_LEFT_FORWARD,
        PUSH_BEACON,
        IDLE
    }

    public enum AllianceColor {
        RED,
        BLUE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private FrontBeaconControlState frontBeaconControlState;

    private AllianceColor allianceColor = AllianceColor.RED;

    private FrontBeaconPusher frontBeaconPusher;

    private FrontBeaconPusher.BeaconPusherState frontBeaconPusherState;

    private FrontBeaconPusher.BeaconColor beaconColor;

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

    public FrontBeaconPusherControl(HardwareMap hardwareMap, Telemetry telemetry, MuxPlusColorSensors muxPlusColorSensors, AllianceColor allianceColor) {
        frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        this.allianceColor = allianceColor;
        frontBeaconControlState = FrontBeaconControlState.IDLE;
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

    public void startBeaconControl() {
        frontBeaconControlState = FrontBeaconControlState.LOOK_FOR_BEACON;
    }

    public FrontBeaconControlState update() {
        frontBeaconPusherState = frontBeaconPusher.updateState();
        beaconColor = frontBeaconPusher.getBeaconColor();
        switch (frontBeaconControlState) {
            case IDLE:
                break;
            case BOTH_BACK:
                frontBeaconControlState = FrontBeaconControlState.MOVE_PUSHERS_TO_MIDDLE;
                frontBeaconPusher.moveBothMidway();
                break;
            case MOVE_PUSHERS_TO_MIDDLE:
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.BOTH_MIDDLE) {
                    frontBeaconControlState = FrontBeaconControlState.AT_MIDDLE;
                }
                if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_BOTH_MIDDLE) {
                    frontBeaconPusher.moveBothMidway();
                }
                break;

            case AT_MIDDLE:
                break;
            case LOOK_FOR_BEACON:
                if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE
                        || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED) {
                    frontBeaconControlState = FrontBeaconControlState.MOVING_RIGHT_FORWARD_LEFT_BACK;
                }
                if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED
                        || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE) {
                    frontBeaconControlState = FrontBeaconControlState.MOVING_LEFT_FORWARD_RIGHT_BACK;
                }
                break;
            case MOVING_RIGHT_FORWARD_LEFT_BACK:
                if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_LEFT_BACK_RIGHT_FORWARD){
                    frontBeaconPusher.moveLeftPusherBackRightPusherForward();
                }
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_BACK_RIGHT_FORWARD){
                    frontBeaconControlState = FrontBeaconControlState.AT_LEFT_BACK_RIGHT_FORWARD;
                }
                break;
            case MOVING_LEFT_FORWARD_RIGHT_BACK:
                if (frontBeaconPusherState != FrontBeaconPusher.BeaconPusherState.MOVING_TO_LEFT_FORWARD_RIGHT_BACK){
                    frontBeaconPusher.moveLeftPusherForwardRightPusherBack();
                }
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_FORWARD_RIGHT_BACK){
                    frontBeaconControlState = FrontBeaconControlState.AT_RIGHT_BACK_LEFT_FORWARD;
                }
                break;
            case AT_LEFT_BACK_RIGHT_FORWARD:
                frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                break;
            case AT_RIGHT_BACK_LEFT_FORWARD:
                frontBeaconControlState = FrontBeaconControlState.CHECK_BEACON_PUSHED;
                break;
            case CHECK_BEACON_PUSHED:
                frontBeaconControlState = FrontBeaconControlState.MOVE_PUSHERS_TO_MIDDLE;
                break;

            case BOTH_FORWARD:
                break;
        }
        return frontBeaconControlState;
    }

}


