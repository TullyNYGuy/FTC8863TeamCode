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

public class AngleChanger {

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
    //CURRENT ANGLE IS IN RADIANS
    // double currentAngle;

    // internal units are radians
   // private AngleUnit angleUnit = AngleUnit.RADIANS;

    private DcMotor8863 motor;
    private Switch limitSwitch;

    // The current angle is stored in PersistantStorage.shooterAngle
    private final double MAX_ANGLE = AngleUnit.RADIANS.fromDegrees(40);
    private final double MIN_ANGLE = AngleUnit.RADIANS.fromDegrees(0);

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
    public int getMotorTicks(){
        return motor.getBaseEncoderCount();
    }
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

    public void setMotorticks(int motorTicks){
        motor.setBaseEncoderCount(motorTicks);
    }

    public void resetMotor() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public void angleLower(){
        setAngleNegative(AngleUnit.DEGREES, -36);
    }
    public AngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        limitSwitch = new Switch(hardwareMap, UltimateGoalRobotRoadRunner.HardwareName.ANGLECHANGERLIMITSWITCH.hwName, Switch.SwitchType.NORMALLY_OPEN);
     }

    public static void clearAngleChanger(){
        PersistantStorage.setShooterAngle(0, AngleUnit.DEGREES);
    }
    public boolean isSwitchTriggered(){
        return limitSwitch.isPressed();
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



    public void setAngleReference() {
       PersistantStorage.setShooterAngle(0, AngleUnit.RADIANS);
    }

    public void update() {
        motor.update();
    }

    public boolean isAngleAdjustComplete() {
        return (motor.isMovementComplete());
    }

    public boolean init(Configuration config) {
        return true;
    }
}
