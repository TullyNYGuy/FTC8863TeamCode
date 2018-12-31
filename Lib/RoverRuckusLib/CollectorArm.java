package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class CollectorArm {

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
    public DcMotor8863 rotationMotor;
    private Telemetry telemetry;

    private double initPosition = 0;
    private double homePosition = -25;
    private double collectPosition = -140;
    private double transferPosition = -80;
    private double dehangPosition = -42;
    private double clearStarPosition = -55;

    private DcMotor8863 extensionArmMotor;
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
    public CollectorArm(HardwareMap hardwareMap, Telemetry telemetry){
        rotationMotor = new DcMotor8863("collectorArmRotationMotor", hardwareMap, telemetry);
        rotationMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rotationMotor.setMovementPerRev(360*48/128);
        rotationMotor.setMotorToHold();
        rotationMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotationMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        extensionArmMotor =new DcMotor8863("extensionArmMotor", hardwareMap, telemetry);
        extensionArmMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        //72tooth gear on motor and 56tooth gear on lead screw and lead screw moves 8mm per rev
        extensionArmMotor.setMovementPerRev((72/56)*(8/25.4));
        extensionArmMotor.setMotorToHold();
        extensionArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extensionArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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
    public void init(){}

    public DcMotor8863.MotorState update(){
        return (rotationMotor.update());
    }

    public void shutdown(){}

    public void displayEncoder(){
        telemetry.addData("encoder value ", rotationMotor.getCurrentPosition());
        telemetry.addData("arm angle ", rotationMotor.getPositionInTermsOfAttachment());
    }

    public void rotationRunMotorUsingEncoder(){
        rotationMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotationMotor.setPower(0.2);
    }

    public void rotationRunMotorUsingPosition(){
        rotationMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rotationMotor.moveToPosition(0.2,-100, DcMotor8863.FinishBehavior.HOLD);
    }


    public void rotationGoToHome(){
        rotationMotor.moveToPosition(0.2, homePosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationGoToCollect(){
        rotationMotor.moveToPosition(0.2, collectPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationGoToPark() {
        rotationMotor.moveToPosition(0.2, -130, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationGoToTransfer(){
        rotationMotor.moveToPosition(0.2, transferPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationGoToDehang(){
        rotationMotor.moveToPosition(0.2, dehangPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationGoToClearStar(){
        rotationMotor.moveToPosition(0.2, clearStarPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationFloatArm(){
        rotationMotor.setMotorToFloat();
    }

    //*********************************************************************************************]
    // lift motor position feedback
    //**********************************************************************************************

    public int getExtensionMotorEncoder() {
        return extensionArmMotor.getCurrentPosition();
    }

    public void displayExtensionMotorEncoder() {
        telemetry.addData("Encoder = ", getExtensionMotorEncoder());
    }

    public double getLiftPosition() {
        return extensionArmMotor.getPositionInTermsOfAttachment();
    }

    public void displayLiftPosition() {
        telemetry.addData("Lift position (inches) = ", getLiftPosition());
    }

//    public void displayRequestedLiftPosition() {
//        telemetry.addData("Lift position requested (inches) = ", desiredPosition);
//    }

//    public void displayLiftPower() {
//        telemetry.addData("Lift power (inches) = ", liftPower);
//    }

    public void displayMotorState() {
        telemetry.addData("Motor state = ", extensionArmMotor.getCurrentMotorState().toString());
    }
}
