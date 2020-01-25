package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

public class DualLift {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum DualLiftStates {
        START_RESET_SEQUENCE, //
        PERFORMING_PRE_RESET_ACTIONS, // actions that need to be run before mechanism can be moved to reset position
        MOVING_TO_RESET_POSITION, //
        PERFORMING_POST_RESET_ACTIONS, // actions that need to be run after the movement to the reset positon is complete
        RESET_COMPLETE, // reset movement and post reset actions are complete
        START_RETRACTION_SEQUENCE, //
        PERFORMING_PRE_RETRACTION_ACTIONS, // actions that need to be run before mechanism can be moved to retracted position
        RETRACTING, // in process of retracting
        PERFORMING_POST_RETRACTION_ACTIONS, // actions that need to be run after the movement to full retraction is complete
        FULLY_RETRACTED, // fully retracted
        START_EXTENSION_SEQUENCE, //
        PERFORMING_PRE_EXTENSION_ACTIONS, // actions that need to be run before mechanism can be moved extended position
        EXTENDING, // in process of extending
        PERFORMING_POST_EXTENSION_ACTIONS, // actions that need to be run after the movement to full extension is complete
        FULLY_EXTENDED, // fully extended
        START_GO_TO_POSITION, //
        MOVING_TO_POSITION, // moving to a specified position
        AT_POSITION, // arrived at the specified position
        START_JOYSTICK, //
        JOYSTICK // under joystick control
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Lift liftRight;
    private String liftRightName = "LiftRight";
    private String liftRightExtensionLimitSwitchName = "LiftExtensionLimitSwitchRight";
    private String liftRightRetractionLimitSwitch = "LiftRetractionLimitSwitchRight";
    private String liftRightMotorName = "LifMotortRight";

    private Lift liftLeft;
    private String liftLeftName = "LiftLeft";
    private String liftLeftExtensionLimitSwitchName = "LiftExtensionLimitSwitchLeft";
    private String liftLeftRetractionLimitSwitch = "LiftRetractionLimitSwitchLeft";
    private String liftLeftMotorName = "LiftMotorLeft";

    private DcMotor8863.MotorType motorType = org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_40;
    private double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    private double movementPerRevolution = spoolDiameter * Math.PI * 5;

    private int maxBlockNumber = 6;

    private double heightAboveTower = 1;

    private DataLogging logFileBoth;



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

    public int getCurrentEncoderValueLeft() {
        return liftLeft.getCurrentEncoderValue();
    }

    public int getCurrentEncoderValueRight() {
        return liftRight.getCurrentEncoderValue();
    }
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
        goToPosition(Skystone.getHeightPlusNubIN() * blockNumber + heightAboveTower, .5);

    }

    public void goToBottom() {
        goToPosition(0.5, 0.5);
    }

    public void lowerBlockOntoTower() {

    }

    public void setRetractionPower(double power){
        liftRight.setRetractionPower(power);
        liftLeft.setRetractionPower(power);
    }

    public void setExtensionPower(double power){
        liftLeft.setExtensionPower(power);
        liftRight.setExtensionPower(power);
    }

    public void setDataLog(DataLogging logFileBoth){
        liftLeft.setDataLog(logFileBoth);
    }

    public boolean isPositionReached() {
        return (liftLeft.isPositionReached() && liftRight.isPositionReached());
    }

    public ExtensionRetractionMechanism.ExtensionRetractionStates update() {
        liftRight.update();
        liftLeft.update();
        //this return is temporary fix it later
        return ExtensionRetractionMechanism.ExtensionRetractionStates.RESET_COMPLETE;
    }

    public void shutdown() {
        liftRight.shutdown();
        liftLeft.shutdown();
    }

}
