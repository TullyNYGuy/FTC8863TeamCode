package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class DualLift {

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

    private Lift liftRight;
    private String liftRightName = "LiftRight";
    private String liftRightExtensionLimitSwitchName = "LiftRightExtensionLimitSwitch";
    private String liftRightRetractionLimitSwitch = "LiftRightRetractionLimitSwitch";
    private String liftRightMotorName = "LiftRightMotor";

    private Lift liftLeft;
    private String liftLeftName = "LiftLeft";
    private String liftLeftExtensionLimitSwitchName = "LiftLeftExtensionLimitSwitch";
    private String liftLeftRetractionLimitSwitch = "LiftLeftRetractionLimitSwitch";
    private String liftLeftMotorName = "LiftLeftMotor";

    private DcMotor8863.MotorType motorType = org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;
    private double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    private double movementPerRevolution = spoolDiameter * Math.PI * 5;

    private int maxBlockNumber = 6;

    public int getMaxBlockNumber() {
        return maxBlockNumber;
    }

    public void setMaxBlockNumber(int maxBlockNumber) {
        this.maxBlockNumber = maxBlockNumber;
    }
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

    public DualLift(HardwareMap hardwareMap, Telemetry telemetry, Double positionPower) {
        liftRight = new Lift(hardwareMap, telemetry, liftRightName,
                liftRightExtensionLimitSwitchName, liftRightRetractionLimitSwitch, liftRightMotorName,
                motorType, movementPerRevolution);
        liftLeft = new Lift(hardwareMap, telemetry, liftLeftName,
                liftLeftExtensionLimitSwitchName, liftLeftRetractionLimitSwitch, liftLeftMotorName,
                motorType, movementPerRevolution);
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

    public void init(){
        liftRight.reset();
        liftLeft.reset();
    }

    public boolean isInitComplete() {
        return (liftRight.isResetComplete() && liftLeft.isResetComplete());
    }

    public void reset() {
        liftRight.reset();
        liftLeft.reset();
    }

    public void goToPosition(double positionInInches, double positionPower) {
        liftRight.goToPosition(positionInInches, positionPower);
        liftLeft.goToPosition(positionInInches, positionPower);
    }

    public void goToBlockHeights(int blockNumber) {
        if (blockNumber > maxBlockNumber) {
            blockNumber = maxBlockNumber;
        }
        goToPosition(5, .5);

    }
    public void update() {
        liftRight.update();
        liftLeft.update();
    }

    public void shutdown() {
        liftRight.shutdown();
        liftLeft.shutdown();
    }

}
