package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class FrontBeaconPusher {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum BeaconPusherState {
        BOTH_BACK,
        BOTH_MIDDLE,
        LEFT_BACK_RIGHT_FORWARD,
        LEFT_FORWARD_RIGHT_BACK,
        BOTH_FORWARD;
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private BeaconPusherState beaconPusherState = BeaconPusherState.BOTH_BACK;

    private CRServo leftCRServo;
    private CRServo rightCRServo;

    private Switch leftFrontLimitSwitch;
    private Switch leftBackLimitSwitch;
    private Switch rightFrontLimitSwitch;
    private Switch rightBackLimitSwitch;
    private AdafruitColorSensor8863 rightColorSensor;

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
}
