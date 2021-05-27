package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

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
    private double currentAngle;

    // internal units are radians
    private AngleUnit angleUnit = AngleUnit.RADIANS;

    private DcMotor8863 motor;

    private final double MAX_ANGLE = Math.toRadians(40);
    private final double MIN_ANGLE = Math.toRadians(0);

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCurrentAngle(AngleUnit desiredUnits) {
        
        return desiredUnits.fromUnit(angleUnit, currentAngle);
    }

    public void setCurrentAngle(AngleUnit units, double desiredAngle) {
       desiredAngle = angleUnit.fromUnit(units, desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        if (desiredAngle < MIN_ANGLE) {
            desiredAngle = MIN_ANGLE;
        }
        this.currentAngle = desiredAngle;

        motor.moveToPosition(1, calculateLeadScrewPosition(angleUnit, desiredAngle), DcMotor8863.FinishBehavior.HOLD);
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    private AngleChanger(HardwareMap hardwareMap, Telemetry telemetry) {
        motor = new DcMotor8863(UltimateGoalRobotRoadRunner.HardwareName.LEAD_SCREW_MOTOR.hwName, hardwareMap, telemetry);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        motor.setMovementPerRev(8);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
    }
    public static AngleChanger createAngleChanger (HardwareMap hardwareMap, Telemetry telemetry){
        if(PersistantStorage.angleChanger == null){
            PersistantStorage.angleChanger = new AngleChanger(hardwareMap, telemetry);
        }
        return PersistantStorage.angleChanger;
    }
    public static void clearAngleChanger(){
        PersistantStorage.angleChanger = null;
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double calculateLeadScrewPosition(AngleUnit units, double desiredAngle) {
        double desiredAngleInRadians = angleUnit.fromUnit(units, desiredAngle);
        //constants
        double initialLength = toMM(1.345);
        double initialAngle = angleUnit.fromDegrees(9.961);
        //Side A is the bottom side side B is the shooter
        double sideA = toMM(6.593);
        double sideB = toMM(7.207);
        if(desiredAngleInRadians > 0){
            double leadScrewPosition = Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngleInRadians + initialAngle)) - initialLength;
            return leadScrewPosition;
        } else {
            desiredAngleInRadians = Math.abs(desiredAngleInRadians);
            double leadScrewPosition = -Math.sqrt(Math.pow(sideA, 2) + Math.pow(sideB, 2) - 2 * sideA * sideB * Math.cos(desiredAngleInRadians + initialAngle)) - initialLength;
            return leadScrewPosition;
        }

    }

    private double toMM(double inches) {
        return inches * 25.4;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setAngleNegative(AngleUnit units, double desiredAngle){
        desiredAngle = angleUnit.fromUnit(units, desiredAngle);
        if (desiredAngle > MAX_ANGLE) {
            desiredAngle = MAX_ANGLE;
        }
        this.currentAngle = desiredAngle;

        motor.moveToPosition(0.3, calculateLeadScrewPosition(angleUnit, desiredAngle), DcMotor8863.FinishBehavior.HOLD);

    }



    public void setAngleReference() {
        currentAngle = 0;

    }

    public void update() {
        motor.update();
    }

    public boolean isAngleAdjustComplete() {
        if (motor.isRotationComplete()) {
            PersistantStorage.angleChanger = this;
            return true;
        } else {
            return false;
        }

    }
    public boolean init(Configuration config) {
        return true;
    }
}
