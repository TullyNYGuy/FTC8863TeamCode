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
        RIGHT_BACK_LEFT_FORWARD
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
        switch (frontBeaconControlState) {
            case BOTH_BACK:
                frontBeaconControlState = FrontBeaconControlState.BOTH_MIDDLE;
                frontBeaconPusher.moveBothMidway();
                break;
            case BOTH_MIDDLE:
                if (allianceColor == AllianceColor.BLUE && frontBeaconPusher.getBeaconColor() == FrontBeaconPusher.BeaconColor.RED_BLUE
                    || allianceColor == AllianceColor.RED && frontBeaconPusher.getBeaconColor() == FrontBeaconPusher.BeaconColor.BLUE_RED) {
                    frontBeaconControlState = FrontBeaconControlState.LEFT_BACK_RIGHT_FORWARD;
                }
                if (allianceColor == AllianceColor.BLUE && frontBeaconPusher.getBeaconColor() == FrontBeaconPusher.BeaconColor.BLUE_RED
                        || allianceColor == AllianceColor.RED && frontBeaconPusher.getBeaconColor() == FrontBeaconPusher.BeaconColor.RED_BLUE) {
                    frontBeaconControlState = FrontBeaconControlState.RIGHT_BACK_LEFT_FORWARD;
                }
                frontBeaconControlState = FrontBeaconControlState.RIGHT_BACK_LEFT_FORWARD;
                break;
            case BOTH_FORWARD:
                break;
            case LEFT_BACK_RIGHT_FORWARD:
                break;
            case RIGHT_BACK_LEFT_FORWARD:
                break;
        }
    }

}
