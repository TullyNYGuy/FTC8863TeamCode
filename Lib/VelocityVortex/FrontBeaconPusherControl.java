package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


public class FrontBeaconPusherControl {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum FrontBeaconControlState {
        BOTH_BACK,
        BOTH_MIDDLE,
        BOTH_FORWARD,
        LEFT_BACK_RIGHT_FORWARD,
        RIGHT_BACK_LEFT_FORWARD,
        PUSH_BEACON
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

    public FrontBeaconControlState update(){
        frontBeaconPusherState = frontBeaconPusher.updateState();
        beaconColor = frontBeaconPusher.getBeaconColor();
        switch (frontBeaconControlState) {
            case BOTH_BACK:
                frontBeaconControlState = FrontBeaconControlState.BOTH_MIDDLE;
                frontBeaconPusher.moveBothMidway();
                break;
            case BOTH_MIDDLE:
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.BOTH_MIDDLE) {
                    if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE
                            || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED) {
                        frontBeaconControlState = FrontBeaconControlState.LEFT_BACK_RIGHT_FORWARD;
                    }
                    if (allianceColor == AllianceColor.BLUE && beaconColor == FrontBeaconPusher.BeaconColor.BLUE_RED
                            || allianceColor == AllianceColor.RED && beaconColor == FrontBeaconPusher.BeaconColor.RED_BLUE) {
                        frontBeaconControlState = FrontBeaconControlState.RIGHT_BACK_LEFT_FORWARD;
                    }
                    frontBeaconControlState = FrontBeaconControlState.RIGHT_BACK_LEFT_FORWARD;
                }
                break;
            case BOTH_FORWARD:
                break;
            case LEFT_BACK_RIGHT_FORWARD:
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_BACK_RIGHT_FORWARD){
                    frontBeaconControlState = FrontBeaconControlState.PUSH_BEACON;
                }
                break;
            case RIGHT_BACK_LEFT_FORWARD:
                if (frontBeaconPusherState == FrontBeaconPusher.BeaconPusherState.LEFT_FORWARD_RIGHT_BACK){
                    frontBeaconControlState = FrontBeaconControlState.PUSH_BEACON;
                }
                break;
            case PUSH_BEACON:
                if (beaconColor != frontBeaconPusher.getBeaconColor()){
                    frontBeaconControlState = FrontBeaconControlState.BOTH_MIDDLE;
                }
                break;
        }
        return frontBeaconControlState;
    }

}
