package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

import java.security.acl.AclNotFoundException;

public class AngleChanger {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum States {
        IDLE,
        MOVING_TO_ZERO,
        MOVING_TO_START_ANGLE
    }

    private States currentState = States.IDLE;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    //CURRENT ANGLE IS IN RADIANS
    // double currentAngle;

    // internal units are radians
   // private AngleUnit angleUnit = AngleUnit.RADIANS;

    private DcMotor8863 motor;
    private Switch limitSwitch;

    // The current angle is stored in PersistantStorage.shooterAngle
    private final double MAX_ANGLE = AngleUnit.RADIANS.fromDegrees(40);
    private final double MIN_ANGLE = AngleUnit.RADIANS.fromDegrees(0);
    private final double START_ANGLE = AngleUnit.RADIANS.fromDegrees(37);

    private boolean resetToZeroComplete = false;
    private boolean startAngleReached = false;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //********************************************************************************************
    public double getCurrentAngle (){
        return PersistantStorage.getShooterAngle(AngleUnit.RADIANS);
    }

    public double getCurrentAngle(AngleUnit desiredUnits) {
        return PersistantStorage.getShooterAngle(desiredUnits);
    }

    public double getStartAngle (AngleUnit desiredUnits) {
        return desiredUnits.fromRadians(START_ANGLE);
    }

    public boolean isResetToZeroComplete() {
        return resetToZeroComplete;
    }

    public boolean isStartAngleReached() {
        return startAngleReached;
    }

    // It is probably not a good idea to allow the general public to be messing with the motor
    // base encoder. You can keep this internal to this class by moving all that functionality
    // into this class.
//    public int getMotorTicks(){
//        return motor.getBaseEncoderCount();
//    }
//
//    public void setMotorticks(int motorTicks){
//        motor.setBaseEncoderCount(motorTicks);
//    }

    public void setCurrentAngle(AngleUnit units, double desiredAngle) {
       desiredAngle = units.toRadians(desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        if (desiredAngle < MIN_ANGLE) {
            desiredAngle = MIN_ANGLE;
        }
        PersistantStorage.setShooterAngle(desiredAngle, AngleUnit.RADIANS);

        motor.moveToPosition(1, calculateLeadScrewPosition(AngleUnit.RADIANS, desiredAngle), DcMotor8863.FinishBehavior.HOLD);
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public AngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        limitSwitch = new Switch(hardwareMap, UltimateGoalRobotRoadRunner.HardwareName.ANGLE_CHANGER_LIMIT_SWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
     }

    public static void clearAngleChanger(){
        PersistantStorage.setShooterAngle(0, AngleUnit.DEGREES);
        PersistantStorage.setMotorTicks(0);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double calculateLeadScrewPosition(AngleUnit units, double desiredAngle) {
        double desiredAngleInRadians = AngleUnit.RADIANS.fromUnit(units, desiredAngle);
        //constants
        double initialLength = DistanceUnit.MM.fromInches(1.345);
        double initialAngle = AngleUnit.RADIANS.fromDegrees(9.961);
        //Side A is the bottom side side B is the shooter
        double sideA = DistanceUnit.MM.fromInches(6.593);
        double sideB = DistanceUnit.MM.fromInches(7.207);
        if(desiredAngleInRadians > 0){
            double leadScrewPosition = Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngleInRadians + initialAngle)) - initialLength;
            return leadScrewPosition;
        } else {
            desiredAngle = Math.abs(desiredAngle);
            double leadScrewPosition = -Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngle + initialAngle)) - initialLength;
            return leadScrewPosition;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setAngleNegative(AngleUnit units, double desiredAngle){
        desiredAngle = AngleUnit.RADIANS.fromUnit(units, desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        PersistantStorage.setShooterAngle( desiredAngle, AngleUnit.RADIANS); ;

        motor.moveToPosition(0.3, calculateLeadScrewPosition(AngleUnit.RADIANS, desiredAngle), DcMotor8863.FinishBehavior.HOLD);
    }

//    public void setAngleReference() {
//       PersistantStorage.setShooterAngle(0, AngleUnit.RADIANS);
//    }

    public void update() {
        motor.update();
        switch (currentState) {
            case IDLE:
                break;
            case MOVING_TO_ZERO:
                if (limitSwitch.isPressed()) {
                    // turn the motor off
                    motor.setPower(0);
                    // We are about to reset all info about the motor to 0
                    PersistantStorage.setMotorTicks(0);
                    motor.setBaseEncoderCount(0);
                    // save the angle to persistant storage
                    PersistantStorage.setShooterAngle(0, AngleUnit.DEGREES);
                    // reset the motor to zero the encoder kept in the control hub
                    motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    // and the movement to zero is complete
                    resetToZeroComplete = true;
                    currentState = States.IDLE;
                }
                break;
            case MOVING_TO_START_ANGLE:
                if (isAngleAdjustComplete()) {
                    // the shooter has arrived at the desired angle
                    startAngleReached = true;
                    // save the encouder count so we can use it later to set the base encoder count
                    saveAngleInfoForLater();
                    currentState = States.IDLE;
                }
                break;
        }
    }

    public boolean isAngleAdjustComplete() {
        return (motor.isMovementComplete());
    }

    public boolean init(Configuration config) {
        return true;
    }

    public void resetAngleToZero() {
        // set the motor to run under velocity control
        // We don't want to run to position here because the motor will keep running until it gets reset
        // Running under velocity control is better
        // Negative power is decreasing the angle
        motor.runAtConstantSpeed(-.3);
        // we are now starting the reset to zero
        resetToZeroComplete = false;
        currentState = States.MOVING_TO_ZERO;
    }

    public void setToStartAngle() {
        // run the motor in run to position mode
        // just in case this is called from a point when the angle changes was not at zero
        motor.setBaseEncoderCount(PersistantStorage.getMotorTicks());
        // we want the motor to hold its position once it reaches the start angle
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        setCurrentAngle(AngleUnit.RADIANS, START_ANGLE);
        // we are now starting to move to the start angle
        startAngleReached = false;
        currentState = States.MOVING_TO_START_ANGLE;
    }

    public void restoreAngleInfo() {
        motor.setBaseEncoderCount(PersistantStorage.getMotorTicks());
    }

    public void saveAngleInfoForLater() {
        PersistantStorage.setMotorTicks(motor.getCurrentPosition());
    }

    public void displaySwitchStatus(Telemetry telemetry) {
        if (limitSwitch.isPressed()) {
            telemetry.addData("Angle changer limit switch IS PRESSED", "!");
        } else {
            telemetry.addData("Angle changer limit switch IS NOT PRESSED", ".");
        }
    }

    public int getMotorEncoderCount() {
        return motor.getCurrentPosition();
    }

//    public void resetMotor() {
//        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//    }

//    public boolean isSwitchTriggered(){
//        return limitSwitch.isPressed();
//    }

//    public void angleLower(){
//        setAngleNegative(AngleUnit.DEGREES, -36);
//    }
}
